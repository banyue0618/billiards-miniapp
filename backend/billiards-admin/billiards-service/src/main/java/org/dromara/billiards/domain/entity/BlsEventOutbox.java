package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.LocalDateTime;

import java.io.Serial;

/**
 * 本地消息(Outbox)对象 bls_event_outbox
 *
 * @author banyue
 * @date 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_event_outbox")
public class BlsEventOutbox extends BlsTenantMchEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 事件ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 聚合根类型，如 ORDER
     */
    private String aggregateType;

    /**
     * 聚合根ID，如订单ID
     */
    private String aggregateId;

    /**
     * 事件类型，如 ORDER_COMPLETED/REFUND_REQUESTED
     */
    private String eventType;

    /**
     * 事件载荷JSON
     */
    private String payload;

    /**
     * 状态：0-NEW 1-SENT 2-FAILED
     */
    private Long status;

    /**
     * 重试次数
     */
    private Long retryCount;

    /**
     * 下次重试时间
     */
    private LocalDateTime nextRetryTime;

    /**
     * 最后一次错误信息
     */
    private String lastError;
}
