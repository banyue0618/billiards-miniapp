package org.dromara.billiards.iot.mqtt;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.iot.config.IoTMqttProperties;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.net.InetAddress;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class MqttClientManager {

    private final ConcurrentHashMap<String, MqttClient> clientPool = new ConcurrentHashMap<>();

    @Resource
    private IoTMqttProperties mqttProperties;

    public void publish(Map<String, Object> cfg, byte[] payload) throws Exception {
        // 配置项必须包含 topic
        String topic = (String) cfg.get("topic");
        String clientId = mqttProperties.getProducerClientId();
        String poolKey = buildKey(mqttProperties.getBroker(), clientId);

        MqttClient client = clientPool.computeIfAbsent(poolKey, k -> createClient(mqttProperties.getBroker(), clientId));
        ensureConnected(client);

        MqttMessage message = new MqttMessage(payload);
        message.setQos((Integer) cfg.getOrDefault("qos", 1));
        Boolean retained = (Boolean) cfg.getOrDefault("retained", Boolean.FALSE);
        message.setRetained(Boolean.TRUE.equals(retained));
        client.publish(topic, message);
    }

    public void subscribe(String clientId, String topic, int qos, IMqttMessageListener listener) throws Exception {
        String poolKey = buildKey(mqttProperties.getBroker(), clientId);
        MqttClient client = clientPool.computeIfAbsent(poolKey, k -> createClient(mqttProperties.getBroker(), clientId));
        ensureConnected(client);
        client.subscribe(topic, qos, listener);
    }

    private String buildKey(String broker, String clientId) {
        return broker + "|" + mqttProperties.getUsername() + "|" + clientId;
    }

    private MqttClient createClient(String broker, String clientId) {
        try {
            MqttClient client = new MqttClient(broker, clientId);
            // 延迟连接，由 ensureConnected 负责
            return client;
        } catch (Exception e) {
            throw new RuntimeException("Create MqttClient failed: " + e.getMessage(), e);
        }
    }

    private void ensureConnected(MqttClient client) throws Exception {
        if (client.isConnected()) return;
        synchronized (client) {
            if (client.isConnected()) return;
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(mqttProperties.getUsername());
            options.setPassword(mqttProperties.getPassword().toCharArray());
            options.setAutomaticReconnect(true);
            options.setConnectionTimeout(5);
            client.connect(options);
        }
    }

    private String defaultClientId() {
        try {
            String host = InetAddress.getLocalHost().getHostName();
            return "billiards-svc-" + host + "-" + UUID.randomUUID();
        } catch (Exception e) {
            return "billiards-svc-" + UUID.randomUUID();
        }
    }

    @PreDestroy
    public void shutdown() {
        clientPool.forEach((k, c) -> {
            try {
                if (c != null && c.isConnected()) c.disconnect();
            } catch (Exception e) {
                log.warn("MQTT disconnect failed for key {}: {}", k, e.getMessage());
            }
            try {
                if (c != null) c.close();
            } catch (Exception e) {
                log.warn("MQTT close failed for key {}: {}", k, e.getMessage());
            }
        });
        clientPool.clear();
    }
}


