package org.dromara.billiards.service.pricing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 计费策略工厂
 */
@Service
@Slf4j
public class PricingStrategyFactory {
    
    private final Map<Integer, PricingStrategy> strategyMap = new HashMap<>();
    
    @Autowired
    public PricingStrategyFactory(
            @Qualifier("standardPricingStrategy") PricingStrategy standardPricingStrategy,
            @Qualifier("ladderPricingStrategy") PricingStrategy ladderPricingStrategy) {
        
        strategyMap.put(1, standardPricingStrategy); // 1-标准计费
        strategyMap.put(2, ladderPricingStrategy);   // 2-阶梯计费
        
        log.info("计费策略工厂初始化完成，已加载{}个策略", strategyMap.size());
    }
    
    /**
     * 获取计费策略
     * @param ruleType 规则类型 1-标准计费 2-阶梯计费
     * @return 对应的计费策略
     */
    public PricingStrategy getStrategy(Integer ruleType) {
        PricingStrategy strategy = strategyMap.get(ruleType);
        if (strategy == null) {
            log.warn("未找到类型为{}的计费策略，将使用标准计费策略", ruleType);
            return strategyMap.get(1); // 默认使用标准计费
        }
        return strategy;
    }
    
    /**
     * 注册新策略
     * @param ruleType 规则类型
     * @param strategy 策略实现
     */
    public void registerStrategy(Integer ruleType, PricingStrategy strategy) {
        strategyMap.put(ruleType, strategy);
        log.info("注册计费策略，类型:{}, 实现类:{}", ruleType, strategy.getClass().getSimpleName());
    }
} 