package org.dromara.billiards.service.pricing;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 计费结果封装类
 */
@Data
public class PricingResult {
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
     * 标准单价
     */
    private BigDecimal priceUnit;

    /**
     * 会员单价
     */
    private BigDecimal memberPrice;

    /**
     * 阶梯计费规则
     */
    private String ladderRules;

    /**
     * 会员折扣
     */
    private BigDecimal memberDiscount;

    /**
     * 结算时间
     */
    private LocalDateTime endTime;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 使用时长(分钟)
     */
    private Integer duration;
}
