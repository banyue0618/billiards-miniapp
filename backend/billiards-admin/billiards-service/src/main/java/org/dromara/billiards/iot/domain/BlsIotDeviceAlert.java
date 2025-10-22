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
 * 设备告警（记录设备异常信息）对象 bls_iot_device_alert
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_iot_device_alert")
public class BlsIotDeviceAlert extends BlsTenantMchEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 告警类型：offline/control_failed等
     */
    private String alertType;

    /**
     * 告警级别：info/warning/error/critical
     */
    private String alertLevel;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 关联台桌ID
     */
    private String tableId;

    /**
     * 所属门店ID
     */
    private String storeId;

    /**
     * 告警内容描述
     */
    private String alertContent;

    /**
     * 告警详细数据（如失败次数、成功率等）
     */
    private String alertData;

    /**
     * 处理状态：pending/resolved/ignored
     */
    private String status;

    /**
     * 处理人
     */
    private String handler;

    /**
     * 处理时间
     */
    private Date handleTime;

    /**
     * 处理备注
     */
    private String handleRemark;

    /**
     * 解决时间
     */
    private Date resolvedTime;

}
