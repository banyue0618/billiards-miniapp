package org.dromara.billiards.service.pricing.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.billiards.domain.entity.Order;
import org.dromara.billiards.domain.entity.PriceRule;
import org.dromara.billiards.service.pricing.PricingResult;
import org.dromara.billiards.service.pricing.PricingStrategy;
import org.dromara.common.json.utils.JsonUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

/**
 * 阶梯计费策略实现
 */
@Service("ladderPricingStrategy")
@Slf4j
public class LadderPricingStrategy implements PricingStrategy {
    @Override
    public PricingResult calculatePrice(Order order, PriceRule priceRule, int minutes, boolean isMember) {
        PricingResult result = new PricingResult();

        try {
            // 计算阶梯价格
            BigDecimal amount = calculateLadderAmount(priceRule, minutes, isMember);

            // 设置结果
            result.setOriginalAmount(amount);
            result.setDiscountAmount(BigDecimal.ZERO); // 这里可以扩展增加其他折扣逻辑
            result.setActualAmount(calculateActualAmount(priceRule.getMaxPrice(), amount));

        } catch (Exception e) {
            log.error("计算阶梯价格出错", e);
            // 出错时使用基础单价计算
            BigDecimal amount = priceRule.getPriceUnit().multiply(new BigDecimal(minutes))
                .setScale(2, RoundingMode.HALF_UP);
            result.setOriginalAmount(amount);
            result.setActualAmount(amount);
        }

        result.setPriceUnit(priceRule.getPriceUnit());
        result.setMemberPrice(priceRule.getMemberPrice());
        result.setLadderRules(priceRule.getLadderRules());
        result.setMemberDiscount(priceRule.getMemberDiscount());

        return result;
    }

    /**
     * 计算阶梯价格
     * @param priceRule 价格规则
     * @param minutes 使用分钟数
     * @param isMember 是否是会员
     * @return 计算后的金额
     */
    private BigDecimal calculateLadderAmount(PriceRule priceRule, int minutes, boolean isMember) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        // 从规则中解析阶梯价格设置
        // ladderRules格式示例:
        // [{"startMinute":0,"endMinute":60,"price":0.5,"memberPrice":0.4},
        //  {"startMinute":60,"endMinute":120,"price":0.4,"memberPrice":0.3},
        //  {"startMinute":120,"endMinute":-1,"price":0.3,"memberPrice":0.2}]

        String ladderRulesJson = priceRule.getLadderRules();
        if (StringUtils.isBlank(ladderRulesJson)) {
            // 如果没有设置阶梯规则，则使用基础单价
            return priceRule.getPriceUnit().multiply(new BigDecimal(minutes));
        }

        // 解析阶梯规则
        List<Map<String, Object>> ladderRules = JsonUtils.parseArray(ladderRulesJson, Map.class);

        for (Map<String, Object> ladder : ladderRules) {
            int startMinute = Integer.parseInt(ladder.get("startMinute").toString());
            int endMinute = Integer.parseInt(ladder.get("endMinute").toString());
            BigDecimal price;

            // 选择普通价格或会员价格
            if (isMember && ladder.containsKey("memberPrice")) {
                price = new BigDecimal(ladder.get("memberPrice").toString());
            } else {
                price = new BigDecimal(ladder.get("price").toString());
            }

            // 计算当前阶梯的时长
            int ladderMinutes;
            if (minutes <= startMinute) {
                // 未达到该阶梯
                continue;
            } else if (endMinute == -1 || minutes >= endMinute) {
                // 当前使用时长超过了该阶梯的结束时间
                // -1表示无上限
                ladderMinutes = endMinute == -1 ? minutes - startMinute : endMinute - startMinute;
            } else {
                // 当前使用时长在该阶梯内
                ladderMinutes = minutes - startMinute;
            }

            // 计算该阶梯的费用并累加
            BigDecimal ladderAmount = price.multiply(new BigDecimal(ladderMinutes));
            totalAmount = totalAmount.add(ladderAmount);

            // 如果已经是最后一个阶梯或者当前使用时长已经在某个阶梯内，结束循环
            if (endMinute == -1 || minutes <= endMinute) {
                break;
            }
        }

        return totalAmount;
    }
}
