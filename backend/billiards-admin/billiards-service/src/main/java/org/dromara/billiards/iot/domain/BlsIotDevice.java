package org.dromara.billiards.iot.domain;

import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * IoT设备对象 bls_iot_device
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_iot_device")
public class BlsIotDevice extends BlsTenantMchEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 设备唯一编号
     */
    private String code;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 设备类型：light/lock/speaker/other
     */
    private String type;

    /**
     * 协议类型：mqtt/http/modbus
     */
    private String protocol;

    /**
     * 协议配置（topic/ip/port等）JSON串，如果是MQTT协议则存储topic等信息，如果是HTTP协议则存储IP和端口等信息（包含请求头）
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
