package org.dromara.billiards.service.pricing.impl;

import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.entity.BlsPriceRule;
import org.dromara.billiards.service.pricing.PricingResult;
import org.dromara.billiards.service.pricing.PricingStrategy;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 标准计费策略实现
 */
@Service("standardPricingStrategy")
public class StandardPricingStrategy implements PricingStrategy {
    @Override
    public PricingResult calculatePrice(BlsOrder blsOrder, BlsPriceRule blsPriceRule, int minutes, boolean isMember) {
        PricingResult result = new PricingResult();

        // 根据会员状态选择单价
        BigDecimal priceUnit;
        if (isMember && blsPriceRule.getMemberPrice() != null) {
            priceUnit = blsPriceRule.getMemberPrice();
        } else {
            priceUnit = blsPriceRule.getPriceUnit();
        }

        // 计算原始金额
        BigDecimal amount = priceUnit.multiply(new BigDecimal(minutes));

        // 设置结果
        result.setOriginalAmount(amount);
        result.setDiscountAmount(BigDecimal.ZERO); // 这里可以扩展增加其他折扣逻辑
        result.setActualAmount(calculateActualAmount(blsPriceRule.getMaxPrice(), amount));

        result.setPriceUnit(blsPriceRule.getPriceUnit());
        result.setMemberPrice(blsPriceRule.getMemberPrice());
        result.setLadderRules(blsPriceRule.getLadderRules());
        result.setMemberDiscount(blsPriceRule.getMemberDiscount());

        return result;
    }
}
