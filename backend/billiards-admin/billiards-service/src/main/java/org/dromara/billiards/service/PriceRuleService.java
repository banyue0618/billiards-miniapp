package org.dromara.billiards.service;

import org.dromara.billiards.domain.entity.BlsPriceRule;
import org.dromara.billiards.domain.bo.PriceRuleDto;
import org.dromara.billiards.domain.vo.PriceRulePreviewVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 计费规则服务接口
 */
public interface PriceRuleService extends IService<BlsPriceRule> {

    /**
     * 获取计费规则详情
     * @param id 规则ID
     * @return 计费规则信息
     */
    BlsPriceRule getPriceRuleInfo(String id);

    /**
     * 创建计费规则
     * @param priceRuleDto 计费规则对象
     * @return 是否成功
     */
    BlsPriceRule createPriceRule(PriceRuleDto priceRuleDto);

    /**
     * 更新计费规则
     * @param id 规则ID
     * @param priceRuleDto 计费规则对象
     * @return 是否成功
     */
    BlsPriceRule updatePriceRule(String id, PriceRuleDto priceRuleDto);

    /**
     * 删除计费规则
     * @param id 规则ID
     * @return 是否成功
     */
    boolean deletePriceRule(String id);

    /**
     * 更新计费规则状态
     * @param id 规则ID
     * @param status 状态
     * @return 是否成功
     */
    boolean updatePriceRuleStatus(String id, Integer status);

    /**
     * 获取商家所有计费规则
     * @return 计费规则列表
     */
    List<BlsPriceRule> listPriceRules();

    /**
     * 获取商家计费规则
     * @param merchantId 商户id
     * @return 计费规则列表
     */
    List<BlsPriceRule> listPriceRulesByMerchantId(String merchantId, int ruleType);

    /**
     * 计算费用
     * @param priceRuleId 计费规则ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isMember 是否是会员
     * @return 计算金额
     */
    BigDecimal calculateAmount(String priceRuleId, LocalDateTime startTime, LocalDateTime endTime, boolean isMember);

    /**
     * 预览计费规则计算结果
     * @param ruleId 规则ID
     * @param minutes 使用分钟数
     * @param isMember 是否会员
     * @return 计费预览结果
     */
    PriceRulePreviewVO previewPriceCalculation(String ruleId, Integer minutes, Boolean isMember);

    /**
     * 应用计费规则到指定桌台
     * @param ruleId 计费规则ID
     * @param tableIds 桌台ID列表
     * @return 是否应用成功
     */
    boolean applyPriceRuleToTables(String ruleId, List<String> tableIds);

    /**
     * 查询指定门店下所有桌台中，标准计费规则（rule_type=1, status=0）的最低price_unit (元/分钟)
     * @param storeId 门店ID
     * @return 最低的标准计费单价 (元/分钟)，如果找不到则返回null
     */
    BigDecimal findMinStandardPriceUnitForStore(String storeId);
}
