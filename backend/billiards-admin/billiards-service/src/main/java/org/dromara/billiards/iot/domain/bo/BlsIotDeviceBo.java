package org.dromara.billiards.iot.domain.bo;

import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.billiards.iot.domain.BlsIotDevice;
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
 * IoT设备业务对象 bls_iot_device
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsIotDevice.class, reverseConvertGenerate = false)
public class BlsIotDeviceBo extends BlsTenantMchEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 设备唯一编号
     */
    @NotBlank(message = "设备唯一编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String code;

    /**
     * 设备名称
     */
    @NotBlank(message = "设备名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 设备类型：light/lock/speaker/other
     */
    @NotBlank(message = "设备类型：light/lock/speaker/other不能为空", groups = { AddGroup.class, EditGroup.class })
    private String type;

    /**
     * 协议类型：mqtt/http/modbus
     */
    @NotBlank(message = "协议类型：mqtt/http/modbus不能为空", groups = { AddGroup.class, EditGroup.class })
    private String protocol;

    /**
     * 协议配置（topic/ip/port等）
     */
    private String protocolConfig;

    /**
     * 设备状态：online/offline/error
     */
    private String status;

    /**
     * 最后心跳时间
     */
    private Date lastHeartbeat;

    /**
     * 备注信息
     */
    private String remark;


}
