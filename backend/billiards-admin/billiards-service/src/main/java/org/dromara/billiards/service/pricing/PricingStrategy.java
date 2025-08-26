package org.dromara.billiards.service.pricing;

import org.dromara.billiards.domain.entity.Order;
import org.dromara.billiards.domain.entity.PriceRule;

import java.math.BigDecimal;

/**
 * 计费策略接口
 */
public interface PricingStrategy {
    /**
     * 计算费用
     * @param order 订单 预留参数，以后考虑删除此参数
     * @param priceRule 计费规则
     * @param duration 使用分钟数
     * @param isMember 是否是会员
     * @return 计算结果，包含原始金额、折扣金额和实际金额
     */
    PricingResult calculatePrice(Order order, PriceRule priceRule, int duration, boolean isMember);


    /**
     *
     * @param maxPrice 封顶价格
     * @param originalAmount 原始金额
     * @return
     */
    default BigDecimal calculateActualAmount(BigDecimal maxPrice, BigDecimal originalAmount) {
        // 如果有封顶价格，使用封顶价格
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0 && originalAmount.compareTo(maxPrice) > 0) {
            return maxPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
        }
        // 否则返回原始金额
        return originalAmount.setScale(2, BigDecimal.ROUND_HALF_UP);

    }
}
