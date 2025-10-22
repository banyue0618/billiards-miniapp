package org.dromara.billiards.iot.subscriber;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceHeartbeatLogBo;
import org.dromara.billiards.iot.mqtt.MqttClientManager;
import org.dromara.billiards.iot.config.IoTMqttProperties;
import org.dromara.billiards.iot.service.IBlsIotDeviceHeartbeatLogService;
import org.eclipse.paho.client.mqttv3.IMqttMessageListener;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeviceHeartbeatSubscriber {

    private final MqttClientManager mqttClientManager;
    private final IBlsIotDeviceHeartbeatLogService heartbeatLogService;
    private final IoTMqttProperties mqttProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            String topic = mqttProperties.getHeartbeatTopicPattern();
            mqttClientManager.subscribe(mqttProperties.getConsumerClientId(), mqttProperties.getHeartbeatTopicPattern(), 1, messageListener());
            log.info("Subscribed heartbeat topic: {} via {}", topic, mqttProperties.getBroker());
        } catch (Exception e) {
            log.error("Subscribe heartbeat topic failed", e);
        }
    }

    private IMqttMessageListener messageListener() {
        return (topic, message) -> handleHeartbeat(topic, message);
    }

    private void handleHeartbeat(String topic, MqttMessage message) {
        try {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            Map<String, Object> map = objectMapper.readValue(payload, new TypeReference<Map<String, Object>>(){});
            String deviceCode = str(map.getOrDefault("deviceCode", map.getOrDefault("clientId", "")));
            String ip = str(map.get("ip"));
            Long status = Long.valueOf(str(map.get("status"))); // 设备状态
            Long uptime = Long.valueOf(str(map.get("uptime")));

            BlsIotDeviceHeartbeatLogBo bo = new BlsIotDeviceHeartbeatLogBo();
            bo.setDeviceCode(deviceCode);
            bo.setIpAddress(ip);
            bo.setHeartbeatTime(new Date(uptime));
            heartbeatLogService.insertByBo(bo);

        } catch (Exception e) {
            log.warn("Handle heartbeat failed. topic={}, err={}", topic, e.getMessage());
        }
    }

    private String str(Object v) { return v == null ? null : String.valueOf(v); }
}


