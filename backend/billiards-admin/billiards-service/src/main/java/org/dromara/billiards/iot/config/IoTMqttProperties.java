package org.dromara.billiards.iot.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "billiards.iot.mqtt")
public class IoTMqttProperties {
    /** 统一 MQTT Broker，例如 tcp://emqx:1883 */
    private String broker = "tcp://emqx:1883";
    /** 可选鉴权 */
    private String username;
    private String password;
    /** 生产者 clientId */
    private String producerClientId = "billiards-table-controller";
    /** 心跳消费者 clientId */
    private String consumerClientId = "billiards-heartbeat-consumer";
    /** 订阅的心跳主题通配 */
    private String heartbeatTopicPattern = "billiards/+/+/status";
}


