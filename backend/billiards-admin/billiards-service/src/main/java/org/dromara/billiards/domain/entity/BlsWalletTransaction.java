package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.math.BigDecimal;

/**
 * 用户钱包流水对象 bls_wallet_transaction
 *
 * @author banyue
 * @date 2025-06-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_wallet_transaction")
public class BlsWalletTransaction extends BilliardsBaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * 用户ID
     */
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
     * 关联记录ID(进出帐的记录id,如充值记录ID或消费记录ID)
     */
    private String relatedId;

    /**
     * 来源的充值记录（暂时无用）
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
