package org.dromara.billiards.iot.domain.bo;

import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.billiards.iot.domain.BlsIotDeviceBinding;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 设备业务绑定（定义场景与设备动作映射）业务对象 bls_iot_device_binding
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsIotDeviceBinding.class, reverseConvertGenerate = false)
public class BlsIotDeviceBindingBo extends BlsTenantMchEntity {

    /**
     * 主键
     */
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
    @NotBlank(message = "业务场景：open_table/close_table/timeout等不能为空", groups = { AddGroup.class, EditGroup.class })
    private String scene;

    /**
     * 设备编号（外键）
     */
    @NotBlank(message = "设备编号（外键）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceCode;

    /**
     * 控制命令：turn_on/turn_off/play_audio等
     */
    @NotBlank(message = "控制命令：turn_on/turn_off/play_audio等不能为空", groups = { AddGroup.class, EditGroup.class })
    private String command;

    /**
     * 命令参数（如音量、文件名等）
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


}
