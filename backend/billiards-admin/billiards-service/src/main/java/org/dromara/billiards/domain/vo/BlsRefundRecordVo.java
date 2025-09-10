package org.dromara.billiards.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import org.dromara.billiards.domain.entity.BlsRefundRecord;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 退款记录视图对象 bls_refund_record
 *
 * @author banyue
 * @date 2025-06-08
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = BlsRefundRecord.class)
public class BlsRefundRecordVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @ExcelProperty(value = "记录ID")
    private String id;

    /**
     * bls_pay_record的充值记录id
     */
    @ExcelProperty(value = "bls_pay_record的充值记录id")
    private String payRecordId;

    /**
     * bls_order订单记录id
     */
    @ExcelProperty(value = "bls_order订单记录id")
    private String orderId;

    /**
     * 用户ID
     */
    @ExcelProperty(value = "用户ID")
    private Long userId;

    /**
     * 退款金额
     */
    @ExcelProperty(value = "退款金额")
    private BigDecimal amount;

    /**
     * 退款状态 0-退款中 1-退款成功 2-退款失败
     */
    @ExcelProperty(value = "退款状态 0-退款中 1-退款成功 2-退款失败")
    private Integer refundStatus;

    /**
     * 回调通知时间
     */
    @ExcelProperty(value = "回调通知时间")
    private LocalDateTime notifyTime;

    /**
     * 回调原始数据
     */
    @ExcelProperty(value = "回调原始数据")
    private String notifyData;

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
