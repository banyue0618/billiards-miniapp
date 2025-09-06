package org.dromara.billiards.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsWalletAccount;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * 用户钱包账户视图对象 bls_wallet_account
 *
 * @author banyue
 * @date 2025-06-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsWalletAccount.class)
public class BlsWalletAccountVo implements Serializable {

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
     * 当前余额
     */
    @ExcelProperty(value = "当前余额")
    private BigDecimal balance;

    /**
     * 累计充值
     */
    @ExcelProperty(value = "累计充值")
    private BigDecimal totalRecharge;

    /**
     * 累计退款
     */
    @ExcelProperty(value = "累计退款")
    private BigDecimal totalRefund;

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
