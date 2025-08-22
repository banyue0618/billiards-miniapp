package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.io.Serial;

/**
 * 退款记录对象 bls_refund_record
 *
 * @author banyue
 * @date 2025-06-08
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_refund_record")
public class BlsRefundRecord extends BilliardsBaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "id")
    private String id;

    /**
     * bls_pay_record的充值记录id
     */
    private String payRecordId;

    /**
     * 微信支付交易号
     */
    private String transactionId;

    /**
     * bls_order订单记录id
     */
    private String orderId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 退款金额
     */
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

    /**
     * 上一次主动查询时间
     */
    private LocalDateTime lastQueryTime;
}
