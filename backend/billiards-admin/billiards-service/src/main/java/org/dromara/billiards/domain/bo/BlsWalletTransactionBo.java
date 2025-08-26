package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsWalletTransaction;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 用户钱包流水业务对象 bls_wallet_transaction
 *
 * @author banyue
 * @date 2025-06-08
 */
@Data
@AutoMapper(target = BlsWalletTransaction.class, reverseConvertGenerate = false)
public class BlsWalletTransactionBo {

    /**
     * 记录ID
     */
    @NotBlank(message = "记录ID不能为空", groups = { EditGroup.class })
    private String id;

    /**
     * 用户ID
     */
    @NotNull(message = "用户ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long userId;

    /**
     * 交易类型:RECHARGE/CONSUME/REFUND
     */
    private String transType;

    /**
     * 交易金额
     */
    private BigDecimal amount;

    /**
     * 关联记录ID（如PayRecord或Order）
     */
    @NotBlank(message = "关联记录ID（如PayRecord或Order）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String relatedId;

    /**
     * 来源的充值记录（用于退款）
     */
    private String sourcePayId;

    /**
     * 微信交易id
     */
    private String transactionId;

    /**
     * 备注
     */
    private String remark;
}
