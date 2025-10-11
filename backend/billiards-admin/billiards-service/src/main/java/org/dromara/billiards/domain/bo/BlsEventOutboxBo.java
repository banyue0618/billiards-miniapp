package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsEventOutbox;
import org.dromara.billiards.domain.entity.BlsTenantMchEntity;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 本地消息(Outbox)业务对象 bls_event_outbox
 *
 * @author banyue
 * @date 2025-09-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = BlsEventOutbox.class, reverseConvertGenerate = false)
public class BlsEventOutboxBo extends BlsTenantMchEntity {

    /**
     * 事件ID
     */
    @NotBlank(message = "事件ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 商家ID
     */
    @NotBlank(message = "商家ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String merchantId;

    /**
     * 聚合根类型，如 ORDER
     */
    @NotBlank(message = "聚合根类型，如 ORDER不能为空", groups = { AddGroup.class, EditGroup.class })
    private String aggregateType;

    /**
     * 聚合根ID，如订单ID
     */
    @NotBlank(message = "聚合根ID，如订单ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private String aggregateId;

    /**
     * 事件类型，如 ORDER_COMPLETED/REFUND_REQUESTED
     */
    @NotBlank(message = "事件类型，如 ORDER_COMPLETED/REFUND_REQUESTED不能为空", groups = { AddGroup.class, EditGroup.class })
    private String eventType;

    /**
     * 事件载荷JSON
     */
    @NotBlank(message = "事件载荷JSON不能为空", groups = { AddGroup.class, EditGroup.class })
    private String payload;

    /**
     * 状态：0-NEW 1-SENT 2-FAILED
     */
    @NotNull(message = "状态：0-NEW 1-SENT 2-FAILED不能为空", groups = { AddGroup.class, EditGroup.class })
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

    /**
     * 删除标志（0存在 1删除）
     */
    private Long isDelete;


}
