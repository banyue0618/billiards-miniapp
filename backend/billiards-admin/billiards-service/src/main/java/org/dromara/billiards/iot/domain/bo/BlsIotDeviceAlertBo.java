package org.dromara.billiards.iot.domain.bo;

import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.billiards.iot.domain.BlsIotDeviceAlert;
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
 * 设备告警（记录设备异常信息）业务对象 bls_iot_device_alert
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsIotDeviceAlert.class, reverseConvertGenerate = false)
public class BlsIotDeviceAlertBo extends BlsTenantMchEntity {

    /**
     * 主键
     */
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


}
