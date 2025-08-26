package org.dromara.billiards.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bls_order")
public class Order extends BilliardsBaseEntity {

    /**
     * 订单ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 下单渠道
     */
    private String channel;

    /**
     * 门店ID
     */
    private String storeId;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 桌台ID
     */
    private String tableId;

    /**
     * 桌台编号
     */
    private String tableNumber;

    /**
     * 计费规则ID
     */
    private String priceRuleId;

    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    /**
     * 使用时长(分钟)
     */
    private Integer duration;

    /**
     * 原始金额
     */
    private BigDecimal originalAmount;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 实际金额
     */
    private BigDecimal actualAmount;

    /**
     * 支付状态 0-未支付 1-已支付 2-退款中(同于订单的结算，这与充值记录的支付状态无关)
     */
    private Integer paymentStatus;

    /**
     * 状态 0-进行中 1-已完成
     */
    private Integer status;

    /**
     * 完成标记 1-用户结束 2-管理员结束 3-超时结束
     */
    private Integer completeFlag;

    /**
     * 备注
     */
    private String remark;
}
