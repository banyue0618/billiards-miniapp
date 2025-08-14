package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsRefundRecord;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 退款记录业务对象 bls_refund_record
 *
 * @author banyue
 * @date 2025-06-08
 */
@Data
@AutoMapper(target = BlsRefundRecord.class, reverseConvertGenerate = false)
public class BlsRefundRecordBo {

    /**
     * 记录ID
     */
    @NotBlank(message = "记录ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * bls_pay_record的充值记录id
     */
    @NotBlank(message = "bls_pay_record的充值记录id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String payRecordId;

    /**
     * bls_order订单记录id
     */
    @NotBlank(message = "bls_order订单记录id不能为空", groups = { AddGroup.class, EditGroup.class })
    private String orderId;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 退款金额
     */
    @NotNull(message = "退款金额不能为空", groups = { AddGroup.class, EditGroup.class })
    private BigDecimal amount;

    /**
     * 退款状态 0-退款中 1-退款成功 2-退款失败
     */
    private Integer refundStatus;

    /**
     * 回调通知时间
     */
    private LocalDateTime notifyTime;

    /**
     * 回调原始数据
     */
    private String notifyData;

    /**
     * 备注
     */
    private String remark;
}
