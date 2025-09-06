package org.dromara.billiards.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsWalletTransaction;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 用户钱包流水视图对象 bls_wallet_transaction
 *
 * @author banyue
 * @date 2025-06-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsWalletTransaction.class)
public class BlsWalletTransactionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @ExcelProperty(value = "记录ID")
    private String id;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 交易类型:RECHARGE/CONSUME/REFUND
     */
    @ExcelProperty(value = "交易类型:RECHARGE/CONSUME/REFUND")
    private String transType;

    /**
     * 交易金额
     */
    @ExcelProperty(value = "交易金额")
    private BigDecimal amount;

    /**
     * 关联记录ID（如PayRecord或Order）
     */
    @ExcelProperty(value = "关联记录ID", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "如=PayRecord或Order")
    private String relatedId;

    /**
     * 来源的充值记录（用于退款）
     */
    @ExcelProperty(value = "来源的充值记录", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "用=于退款")
    private String sourcePayId;

    /**
     * 微信交易id
     */
    @ExcelProperty(value = "微信交易id")
    private String transactionId;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @ExcelProperty(value = "删除标志", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "0=代表存在,1=代表删除")
    private Long isDelete;


}
