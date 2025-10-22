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
 * 设备控制日志（记录执行命令历史）对象 bls_iot_control_log
 *
 * @author banyue
 * @date 2025-10-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_iot_control_log")
public class BlsIotControlLog extends BlsTenantMchEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 设备编号
     */
    private String deviceCode;

    /**
     * 控制命令
     */
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
