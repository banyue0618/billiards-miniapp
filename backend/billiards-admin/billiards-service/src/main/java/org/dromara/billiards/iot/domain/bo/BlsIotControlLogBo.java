package org.dromara.billiards.iot.domain.bo;

import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.billiards.iot.domain.BlsIotControlLog;
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
 * 设备控制日志（记录执行命令历史）业务对象 bls_iot_control_log
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsIotControlLog.class, reverseConvertGenerate = false)
public class BlsIotControlLogBo extends BlsTenantMchEntity {

    /**
     * 主键
     */
    private Long id;

    /**
     * 设备编号
     */
    @NotBlank(message = "设备编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String deviceCode;

    /**
     * 控制命令
     */
    @NotBlank(message = "控制命令不能为空", groups = { AddGroup.class, EditGroup.class })
    private String command;

    /**
     * 命令参数
     */
    private String params;

    /**
     * 触发来源：order/admin/system
     */
    private String triggerBy;

    /**
     * 触发场景：open_table/close_table等
     */
    private String triggerScene;

    /**
     * 关联订单ID
     */
    private String orderId;

    /**
     * 关联台桌ID
     */
    private String tableId;

    /**
     * 执行状态：success/failed/timeout/pending
     */
    private String status;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 重试次数
     */
    private Long retryCount;

    /**
     * 执行时间
     */
    private Date executeTime;

    /**
     * 响应耗时（毫秒）
     */
    private Long responseTime;


}
