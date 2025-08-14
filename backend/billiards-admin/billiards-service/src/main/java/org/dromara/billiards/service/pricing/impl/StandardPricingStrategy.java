package org.dromara.billiards.service.pricing.impl;

import org.dromara.billiards.domain.entity.Order;
import org.dromara.billiards.domain.entity.PriceRule;
import org.dromara.billiards.service.pricing.PricingResult;
import org.dromara.billiards.service.pricing.PricingStrategy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 标准计费策略实现
 */
@Service("standardPricingStrategy")
public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public PricingResult calculatePrice(Order order, PriceRule priceRule, int minutes, boolean isMember) {
        PricingResult result = new PricingResult();

        // 根据会员状态选择单价
        BigDecimal priceUnit;
        if (isMember && priceRule.getMemberPrice() != null) {
            priceUnit = priceRule.getMemberPrice();
        } else {
            priceUnit = priceRule.getPriceUnit();
        }

        // 计算原始金额
        BigDecimal amount = priceUnit.multiply(new BigDecimal(minutes));

        // 设置结果
        result.setOriginalAmount(amount);
        result.setDiscountAmount(BigDecimal.ZERO); // 这里可以扩展增加其他折扣逻辑
        result.setActualAmount(calculateActualAmount(priceRule.getMaxPrice(), amount));

        result.setPriceUnit(priceRule.getPriceUnit());
        result.setMemberPrice(priceRule.getMemberPrice());
        result.setLadderRules(priceRule.getLadderRules());
        result.setMemberDiscount(priceRule.getMemberDiscount());

        return result;
    }
}
