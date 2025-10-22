package org.dromara.billiards.iot.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * IoT设备心跳记录对象 bls_iot_device_heartbeat_log
 *
 * @author banyue
 * @date 2025-10-22
 */
@Data
@TableName("bls_iot_device_heartbeat_log")
public class BlsIotDeviceHeartbeatLog {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 设备编码（与iot_device表关联）
     */
    private String deviceCode;

    /**
     * 设备IP地址（可选）
     */
    private String ipAddress;

    /**
     * 固件版本（可选）
     */
    private String firmwareVersion;

    /**
     * 心跳上报时间
     */
    private Date heartbeatTime;

    /**
     * WiFi信号强度（dBm，可选）
     */
    private Long signalStrength;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 商户id
     */
    private String merchantId;

    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
