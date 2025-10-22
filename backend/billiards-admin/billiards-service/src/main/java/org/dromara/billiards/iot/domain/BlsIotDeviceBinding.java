package org.dromara.billiards.iot.domain;

import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 设备业务绑定（定义场景与设备动作映射）对象 bls_iot_device_binding
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_iot_device_binding")
public class BlsIotDeviceBinding extends BlsTenantMchEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 台桌ID（外键）
     */
    private String tableId;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 业务场景：open_table/close_table/timeout等
     */
    private String scene;

    /**
     * 设备编号（外键）
     */
    private String deviceCode;

    /**
     * 控制命令：turn_on/turn_off/play_audio等
     */
    private String command;

    /**
     * 命令参数（如音量、文件名等） '{"file":"goodbye.mp3","volume":80}'
     */
    private String params;

    /**
     * 执行顺序（同场景多个设备时）
     */
    private Long executeOrder;

    /**
     * 是否启用：1启用，0禁用
     */
    private Long enabled;

    /**
     * MQTT主题（仅MQTT协议设备使用）
     */
    private String mqttTopic;
}
