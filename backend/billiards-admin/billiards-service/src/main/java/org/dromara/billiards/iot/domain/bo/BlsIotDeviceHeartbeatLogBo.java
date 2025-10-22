package org.dromara.billiards.iot.domain.bo;

import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.billiards.iot.domain.BlsIotDeviceHeartbeatLog;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * IoT设备心跳记录业务对象 bls_iot_device_heartbeat_log
 *
 * @author banyue
 * @date 2025-10-22
 */
@Data
@AutoMapper(target = BlsIotDeviceHeartbeatLog.class, reverseConvertGenerate = false)
public class BlsIotDeviceHeartbeatLogBo {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 设备编码（与iot_device表关联）
     */
    @NotBlank(message = "设备编码（与iot_device表关联）不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotNull(message = "心跳上报时间不能为空", groups = { AddGroup.class, EditGroup.class })
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

}
