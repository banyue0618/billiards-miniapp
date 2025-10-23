#include <stdio.h>
#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"
#include "esp_system.h"
#include "esp_event.h"
#include "esp_wifi.h"
#include "nvs_flash.h"
#include "esp_log.h"
#include "esp_netif.h"
#include "mqtt_client.h"
#include "driver/gpio.h"
#include "esp_timer.h"

// ===================== 配置区 =====================
#define WIFI_SSID           "YOUR_WIFI_NAME"
#define WIFI_PASS           "YOUR_WIFI_PASSWORD"

#define MQTT_BROKER_URI     "mqtt://192.168.1.100:1883"   // EMQX 地址（tcp）
#define MQTT_CLIENT_ID      "esp32-001"                   // 每台设备唯一ID（即 deviceCode）
#define MQTT_USERNAME       "device001"
#define MQTT_PASSWORD       "abc123"

// 主题示例：billiards/{type}/{code}/command 与 /status
#define TOPIC_SUB_COMMAND   "billiards/light/001/command"
#define TOPIC_PUB_STATUS    "billiards/light/001/status"

#define RELAY_GPIO          GPIO_NUM_5
#define RELAY_ACTIVE_HIGH   1           // 1: 高电平闭合；0: 低电平闭合

#define STATUS_INTERVAL_MS  (30 * 1000) // 心跳上报周期

// ===================== 全局变量 =====================
static const char *TAG = "ESP32-IOT";
static EventGroupHandle_t s_wifi_event_group;
static int s_retry_num = 0;
static char s_ip_str[32] = "0.0.0.0";

// 位定义
#define WIFI_CONNECTED_BIT BIT0

// ===================== GPIO/继电器 =====================
static inline void relay_set(bool on) {
    int level = RELAY_ACTIVE_HIGH ? (on ? 1 : 0) : (on ? 0 : 1);
    gpio_set_level(RELAY_GPIO, level);
}

static inline int relay_status() {
    int level = gpio_get_level(RELAY_GPIO);
    if (RELAY_ACTIVE_HIGH) return level ? 1 : 0; else return level ? 0 : 1;
}

// ===================== WiFi 事件处理 =====================
static void wifi_event_handler(void *arg, esp_event_base_t event_base, int32_t event_id, void *event_data) {
    if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_START) {
        esp_wifi_connect();
    } else if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_DISCONNECTED) {
        if (s_retry_num < 10) {
            esp_wifi_connect();
            s_retry_num++;
            ESP_LOGI(TAG, "retry to connect to the AP");
        }
    } else if (event_base == IP_EVENT && event_id == IP_EVENT_STA_GOT_IP) {
        ip_event_got_ip_t *event = (ip_event_got_ip_t *) event_data;
        snprintf(s_ip_str, sizeof(s_ip_str), IPSTR, IP2STR(&event->ip_info.ip));
        s_retry_num = 0;
        xEventGroupSetBits(s_wifi_event_group, WIFI_CONNECTED_BIT);
        ESP_LOGI(TAG, "got ip: %s", s_ip_str);
    }
}

static void wifi_init_sta(void) {
    s_wifi_event_group = xEventGroupCreate();
    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());
    esp_netif_create_default_wifi_sta();

    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&cfg));

    ESP_ERROR_CHECK(esp_event_handler_instance_register(WIFI_EVENT, ESP_EVENT_ANY_ID, &wifi_event_handler, NULL, NULL));
    ESP_ERROR_CHECK(esp_event_handler_instance_register(IP_EVENT, IP_EVENT_STA_GOT_IP, &wifi_event_handler, NULL, NULL));

    wifi_config_t wifi_config = { 0 };
    strncpy((char *)wifi_config.sta.ssid, WIFI_SSID, sizeof(wifi_config.sta.ssid));
    strncpy((char *)wifi_config.sta.password, WIFI_PASS, sizeof(wifi_config.sta.password));
    wifi_config.sta.threshold.authmode = WIFI_AUTH_WPA2_PSK;
    wifi_config.sta.pmf_cfg.capable = true;
    wifi_config.sta.pmf_cfg.required = false;

    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &wifi_config));
    ESP_ERROR_CHECK(esp_wifi_start());
}

// ===================== MQTT 回调 =====================
static esp_mqtt_client_handle_t s_mqtt_client = NULL;

static void publish_online_state(int online) {
    char buf[32];
    int n = snprintf(buf, sizeof(buf), "{\"online\":%d}", online ? 1 : 0);
    esp_mqtt_client_publish(s_mqtt_client, TOPIC_PUB_STATUS, buf, n, 1, 1);
}

static void publish_status_heartbeat(void) {
    char payload[256];
    int n = snprintf(payload, sizeof(payload),
                     "{\"deviceCode\":\"%s\",\"clientId\":\"%s\",\"status\":%d,\"ip\":\"%s\",\"uptime\":%lld}",
                     MQTT_CLIENT_ID, MQTT_CLIENT_ID, relay_status(), s_ip_str, (long long) (esp_timer_get_time() / 1000));
    esp_mqtt_client_publish(s_mqtt_client, TOPIC_PUB_STATUS, payload, n, 0, 0);
}

static void handle_command_payload(const char *data, int len) {
    // 仅接受纯文本 "0" / "1"
    if (len == 1 && (data[0] == '0' || data[0] == '1')) {
        bool on = (data[0] == '1');
        relay_set(on);
        ESP_LOGI(TAG, "relay %s", on ? "ON" : "OFF");
        // 执行完后上报当前状态（retained=true）
        char buf[64];
        int n = snprintf(buf, sizeof(buf), "{\"status\":%d}", relay_status());
        esp_mqtt_client_publish(s_mqtt_client, TOPIC_PUB_STATUS, buf, n, 1, 1);
    } else {
        ESP_LOGW(TAG, "unknown command payload (expect '0' or '1')");
    }
}

static void mqtt_event_handler(void *handler_args, esp_event_base_t base, int32_t event_id, void *event_data) {
    esp_mqtt_event_handle_t event = event_data;
    switch ((esp_mqtt_event_id_t) event_id) {
        case MQTT_EVENT_CONNECTED:
            ESP_LOGI(TAG, "MQTT connected");
            // 订阅命令主题 QoS1
            esp_mqtt_client_subscribe(s_mqtt_client, TOPIC_SUB_COMMAND, 1);
            // 上线状态 retained
            publish_online_state(1);
            break;
        case MQTT_EVENT_DISCONNECTED:
            ESP_LOGI(TAG, "MQTT disconnected");
            break;
        case MQTT_EVENT_DATA:
            ESP_LOGI(TAG, "MQTT data topic=%.*s data=%.*s", event->topic_len, event->topic, event->data_len, event->data);
            if (event->topic && strncmp(event->topic, TOPIC_SUB_COMMAND, event->topic_len) == 0) {
                handle_command_payload(event->data, event->data_len);
            }
            break;
        default:
            break;
    }
}

static void mqtt_start(void) {
    esp_mqtt_client_config_t mqtt_cfg = {
        .broker.address.uri = MQTT_BROKER_URI,
        .credentials.username = MQTT_USERNAME,
        .credentials.client_id = MQTT_CLIENT_ID,
        .credentials.authentication.password = MQTT_PASSWORD,
        .session.keepalive = 20,
        .session.last_will.topic = TOPIC_PUB_STATUS,
        .session.last_will.msg = "{\"online\":0}",
        .session.last_will.msg_len = 14,
        .session.last_will.qos = 1,
        .session.last_will.retain = true,
    };

    s_mqtt_client = esp_mqtt_client_init(&mqtt_cfg);
    esp_mqtt_client_register_event(s_mqtt_client, ESP_EVENT_ANY_ID, mqtt_event_handler, NULL);
    esp_mqtt_client_start(s_mqtt_client);
}

// ===================== 主任务 =====================
void app_main(void) {
    // NVS & WiFi
    ESP_ERROR_CHECK(nvs_flash_init());
    wifi_init_sta();

    // GPIO init
    gpio_reset_pin(RELAY_GPIO);
    gpio_set_direction(RELAY_GPIO, GPIO_MODE_OUTPUT);
    // 上电默认关闭
    relay_set(false);

    // 等待 WiFi 连接
    xEventGroupWaitBits(s_wifi_event_group, WIFI_CONNECTED_BIT, pdFALSE, pdTRUE, portMAX_DELAY);

    // MQTT
    mqtt_start();

    // 心跳循环
    while (1) {
        publish_status_heartbeat();
        vTaskDelay(pdMS_TO_TICKS(STATUS_INTERVAL_MS));
    }
}
