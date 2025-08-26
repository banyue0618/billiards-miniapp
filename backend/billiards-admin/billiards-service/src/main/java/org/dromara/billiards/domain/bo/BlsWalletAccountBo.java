package org.dromara.billiards.domain.bo;

import org.dromara.billiards.domain.entity.BlsWalletAccount;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * 用户钱包账户业务对象 bls_wallet_account
 *
 * @author banyue
 * @date 2025-06-08
 */
@Data
@AutoMapper(target = BlsWalletAccount.class, reverseConvertGenerate = false)
public class BlsWalletAccountBo{

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
     * 当前余额
     */
    private BigDecimal balance;

    /**
     * 累计充值
     */
    private BigDecimal totalRecharge;

    /**
     * 累计退款
     */
    private BigDecimal totalRefund;

    /**
     * 备注
     */
    private String remark;
}
