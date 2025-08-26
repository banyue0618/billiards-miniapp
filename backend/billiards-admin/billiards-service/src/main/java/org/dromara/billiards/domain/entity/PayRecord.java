package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 充值支付记录表
 *
 * @author zhangsip
 * @date 2024-07-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_pay_record")
public class PayRecord extends BilliardsBaseEntity {

    /**
     * 记录ID
     */
    @TableId
    private String id;

    /**
     * 充值流水号
     */
    private String payNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户微信openid
     */
    private String openid;

    /**
     * 微信支付交易号
     */
    private String transactionId;

    /**
     * 充值金额
     */
    private BigDecimal amount;

    /**
     * 支付渠道（MINIAPP-小程序/APP/H5/WEB）
     */
    private String channel;

    /**
     * 支付状态 详见枚举类：PaymentStatus
     */
    private Integer paymentStatus;

    /**
     * 支付回调通知时间
     */
    private LocalDateTime notifyTime;

    /**
     * 支付回调原始数据
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
