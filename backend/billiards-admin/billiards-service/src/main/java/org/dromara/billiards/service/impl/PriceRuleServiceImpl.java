package org.dromara.billiards.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.billiards.convert.PriceRuleConvert;
import org.dromara.billiards.mapper.PriceRuleMapper;
import org.dromara.billiards.mapper.TableMapper;
import org.dromara.billiards.domain.entity.PriceRule;
import org.dromara.billiards.domain.entity.Table;
import org.dromara.billiards.domain.bo.PriceRuleDto;
import org.dromara.billiards.domain.vo.PriceRulePreviewVO;
import org.dromara.billiards.service.PriceRuleService;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.dromara.billiards.service.pricing.PricingResult;
import org.dromara.billiards.service.pricing.PricingStrategy;
import org.dromara.billiards.service.pricing.PricingStrategyFactory;

/**
 * 计费规则服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class PriceRuleServiceImpl extends ServiceImpl<PriceRuleMapper, PriceRule> implements PriceRuleService {

    private final PriceRuleConvert priceRuleConvert;
    private final TableMapper tableMapper;
    private final PricingStrategyFactory pricingStrategyFactory;

    /**
     * 获取计费规则详情
     * @param id 规则ID
     * @return 计费规则信息
     */
    @Override
    public PriceRule getPriceRuleInfo(String id) {
        if (StringUtils.isEmpty(id)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "规则ID不能为空");
        }
        PriceRule priceRule = this.getById(id);
        if (priceRule == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "计费规则不存在");
        }
        return priceRule;
    }

    /**
     * 创建计费规则
     * @param dto 计费规则DTO
     * @return 创建后的计费规则实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PriceRule createPriceRule(PriceRuleDto dto) {
        // 基于规则类型进行验证
        validatePriceRuleDto(dto);

        PriceRule priceRule = priceRuleConvert.toEntity(dto);
        priceRule.setMerchantId(LoginHelper.getUserIdStr());
        if (!this.save(priceRule)) {
            throw BilliardsException.of(ResultCode.ERROR, "创建计费规则失败");
        }
        return priceRule;
    }

    /**
     * 更新计费规则
     * @param id 规则ID
     * @param dto 计费规则DTO
     * @return 更新后的计费规则实体
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PriceRule updatePriceRule(String id, PriceRuleDto dto) {
        PriceRule existingPriceRule = this.getById(id);
        if (existingPriceRule == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "计费规则不存在，无法更新");
        }

        // 基于规则类型进行验证
        validatePriceRuleDto(dto);

        priceRuleConvert.updateEntityFromDto(dto, existingPriceRule);

        if (!this.updateById(existingPriceRule)) {
            throw BilliardsException.of(ResultCode.ERROR, "更新计费规则失败");
        }
        return existingPriceRule;
    }

    /**
     * 更新计费规则状态
     * @param id 规则ID
     * @param status 状态
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePriceRuleStatus(String id, Integer status) {
        if (StringUtils.isEmpty(id) || status == null) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "规则ID和状态不能为空");
        }
        PriceRule priceRule = this.getById(id);
        if (priceRule == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "计费规则不存在，无法更新状态");
        }
        priceRule.setStatus(status);
        if (!this.updateById(priceRule)) {
            throw BilliardsException.of(ResultCode.ERROR, "更新计费规则状态失败");
        }
        return true;
    }

    /**
     * 删除计费规则
     * @param id 规则ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePriceRule(String id) {
        if (StringUtils.isEmpty(id)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "规则ID不能为空");
        }
        PriceRule priceRule = this.getById(id);
        if (priceRule == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "计费规则不存在，无法删除");
        }

        // 检查是否有桌台正在使用该计费规则
        LambdaQueryWrapper<Table> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Table::getPriceRuleId, id);
        List<Table> boundTables = tableMapper.selectList(queryWrapper);

        if (!boundTables.isEmpty()) {
            // 判断是否有桌台正在使用中（状态为1表示使用中）
            boolean hasTablesInUse = boundTables.stream()
                .anyMatch(table -> table.getStatus() != null && table.getStatus() == 1);

            if (hasTablesInUse) {
                throw BilliardsException.of(ResultCode.ERROR,
                    "计费规则正在被使用中的桌台使用，无法删除。请先停止桌台使用后再删除。");
            }

            // 对已绑定的桌台解除绑定
            log.info("计费规则{}被{}个桌台使用，执行解绑操作", id, boundTables.size());

            // 为所有绑定的桌台设置为null
            LambdaUpdateWrapper<Table> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(Table::getPriceRuleId, id)
                       .set(Table::getPriceRuleId, null);
            tableMapper.update(null, updateWrapper);

            log.info("已解绑计费规则{}的所有桌台", id);
        }

        if (!this.removeById(id)) {
            throw BilliardsException.of(ResultCode.ERROR, "删除计费规则失败");
        }
        return true;
    }

    /**
     * 获取商家所有计费规则
     * @return 计费规则列表
     */
    @Override
    public List<PriceRule> listPriceRules() {
        LambdaQueryWrapper<PriceRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PriceRule::getMerchantId, StpUtil.getLoginIdAsString());
        queryWrapper.orderByDesc(PriceRule::getCreateTime);
        return this.list(queryWrapper);
    }

    @Override
    public List<PriceRule> listPriceRulesByMerchantId(String merchantId, int ruleType) {
        // 现根据门店找到商家，再根据商家获取计费规则
        if (StringUtils.isEmpty(merchantId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "商户ID不能为空");
        }
        LambdaQueryWrapper<PriceRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PriceRule::getMerchantId, merchantId);
        if (ruleType > 0) {
            queryWrapper.eq(PriceRule::getRuleType, ruleType);
        }
        queryWrapper.orderByAsc(PriceRule::getPriceUnit);
        return this.list(queryWrapper);
    }

    /**
     * 计算费用
     * @param priceRuleId 计费规则ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param isMember 是否是会员
     * @return 计算金额
     */
    @Override
    public BigDecimal calculateAmount(String priceRuleId, LocalDateTime startTime, LocalDateTime endTime, boolean isMember) {
        // 获取计费规则
        PriceRule priceRule = this.getById(priceRuleId);
        if (priceRule == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "计费规则不存在");
        }

        // 计算使用分钟数
        long minutes = Duration.between(startTime, endTime).toMinutes();

        // 使用策略工厂获取计费策略
        PricingStrategy pricingStrategy = pricingStrategyFactory.getStrategy(priceRule.getRuleType());

        // 计算费用
        PricingResult pricingResult = pricingStrategy.calculatePrice(null, priceRule, (int) minutes, isMember);
        return pricingResult.getActualAmount();
    }

    /**
     * 预览计费规则计算结果
     * @param ruleId 规则ID
     * @param minutes 使用分钟数
     * @param isMember 是否会员
     * @return 计费预览结果
     */
    @Override
    public PriceRulePreviewVO previewPriceCalculation(String ruleId, Integer minutes, Boolean isMember) {
        // 参数校验
        if (StringUtils.isEmpty(ruleId)) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "计费规则ID不能为空");
        }
        if (minutes == null || minutes <= 0) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "使用分钟数必须大于0");
        }

        // 获取计费规则
        PriceRule priceRule = this.getById(ruleId);
        if (priceRule == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "计费规则不存在");
        }

        // 准备返回结果
        PriceRulePreviewVO result = new PriceRulePreviewVO();
        List<PriceRulePreviewVO.CalculationDetail> details = new ArrayList<>();

        // 使用策略工厂获取计费策略
        PricingStrategy pricingStrategy = pricingStrategyFactory.getStrategy(priceRule.getRuleType());

        // 添加计算详情
        if (priceRule.getRuleType() == 1) {
            // 标准计费详情
            addStandardPricingDetails(priceRule, minutes, isMember, details);
        } else if (priceRule.getRuleType() == 2) {
            // 阶梯计费详情
            addLadderPricingDetails(priceRule, minutes, isMember, details);
        }

        // 计算费用
        PricingResult pricingResult = pricingStrategy.calculatePrice(null, priceRule, minutes, isMember);

        // 设置最终费用
        result.setFee(pricingResult.getActualAmount());
        result.setDetails(details);

        return result;
    }

    /**
     * 添加标准计费详情
     */
    private void addStandardPricingDetails(PriceRule priceRule, Integer minutes, Boolean isMember,
                                          List<PriceRulePreviewVO.CalculationDetail> details) {
        // 选择正确的单价
        BigDecimal priceUnit;
        if (isMember && priceRule.getMemberPrice() != null) {
            priceUnit = priceRule.getMemberPrice();
            details.add(PriceRulePreviewVO.CalculationDetail.builder()
                .description("使用会员价：" + priceUnit.multiply(new BigDecimal(60)) + "元/小时")
                .build());
        } else {
            priceUnit = priceRule.getPriceUnit();
            details.add(PriceRulePreviewVO.CalculationDetail.builder()
                .description("使用标准价：" + priceUnit.multiply(new BigDecimal(60)) + "元/小时")
                .build());
        }

        // 计算原始金额
        BigDecimal originalAmount = priceUnit.multiply(new BigDecimal(minutes));
        details.add(PriceRulePreviewVO.CalculationDetail.builder()
            .description("按时计费：" + priceUnit + "元/分钟 × " + minutes + "分钟 = " + originalAmount.setScale(2, RoundingMode.HALF_UP) + "元")
            .build());

        // 检查最低消费时长
        if (priceRule.getMinMinutes() != null && priceRule.getMinMinutes() > 0 && minutes < priceRule.getMinMinutes()) {
            BigDecimal minAmount = priceUnit.multiply(new BigDecimal(priceRule.getMinMinutes()));
            details.add(PriceRulePreviewVO.CalculationDetail.builder()
                .description("应用最低消费：" + priceRule.getMinMinutes() + "分钟 = " + minAmount.setScale(2, RoundingMode.HALF_UP) + "元")
                .build());
        }

        // 检查封顶价格
        if (priceRule.getMaxPrice() != null && priceRule.getMaxPrice().compareTo(BigDecimal.ZERO) > 0
                && originalAmount.compareTo(priceRule.getMaxPrice()) > 0) {
            details.add(PriceRulePreviewVO.CalculationDetail.builder()
                .description("应用封顶价格：" + priceRule.getMaxPrice() + "元")
                .build());
        }
    }

    /**
     * 添加阶梯计费详情
     */
    private void addLadderPricingDetails(PriceRule priceRule, Integer minutes, Boolean isMember,
                                        List<PriceRulePreviewVO.CalculationDetail> details) {
        // 从规则中解析阶梯价格设置
        String ladderRulesJson = priceRule.getLadderRules();
        if (StringUtils.isBlank(ladderRulesJson)) {
            details.add(PriceRulePreviewVO.CalculationDetail.builder()
                .description("未设置阶梯规则，使用基础价格")
                .build());
            return;
        }

        // 解析阶梯规则
        try {
            List<Map<String, Object>> ladderRules = JsonUtils.parseArray(ladderRulesJson, Map.class);

            details.add(PriceRulePreviewVO.CalculationDetail.builder()
                .description("使用阶梯计费：共" + ladderRules.size() + "个阶梯")
                .build());

            if (isMember && priceRule.getMemberDiscount() != null && priceRule.getMemberDiscount().compareTo(BigDecimal.ZERO) > 0) {
                details.add(PriceRulePreviewVO.CalculationDetail.builder()
                    .description("会员折扣：" + priceRule.getMemberDiscount().multiply(new BigDecimal(10)) + "折")
                    .build());
            }

            // 计费明细太复杂，这里只给出简要说明
            int remainingMinutes = minutes;
            int currentLadder = 1;

            for (Map<String, Object> ladder : ladderRules) {
                int startMinute = getIntValue(ladder.get("startMinute"));
                int endMinute = getIntValue(ladder.get("endMinute"));
                BigDecimal price = getBigDecimalValue(ladder.get("price"));

                if (endMinute == -1 || startMinute < minutes) {
                    // 当前阶梯适用
                    int ladderMinutes = 0;
                    if (endMinute == -1 || minutes <= endMinute) {
                        ladderMinutes = minutes - startMinute;
                    } else {
                        ladderMinutes = endMinute - startMinute;
                    }

                    if (ladderMinutes > 0) {
                        BigDecimal ladderPrice = isMember && priceRule.getMemberDiscount() != null ?
                            price.multiply(priceRule.getMemberDiscount()) : price;

                        details.add(PriceRulePreviewVO.CalculationDetail.builder()
                            .description("第" + currentLadder + "阶梯：" + startMinute + "分钟至" +
                                (endMinute == -1 ? "无限制" : endMinute + "分钟") +
                                "，单价：" + ladderPrice.multiply(new BigDecimal(60)).setScale(2, RoundingMode.HALF_UP) + "元/小时")
                            .build());
                    }
                }

                currentLadder++;
            }

            // 检查封顶价格
            if (priceRule.getMaxPrice() != null && priceRule.getMaxPrice().compareTo(BigDecimal.ZERO) > 0) {
                details.add(PriceRulePreviewVO.CalculationDetail.builder()
                    .description("封顶价格：" + priceRule.getMaxPrice() + "元")
                    .build());
            }

        } catch (Exception e) {
            log.error("解析阶梯规则出错", e);
            details.add(PriceRulePreviewVO.CalculationDetail.builder()
                .description("阶梯规则格式错误，无法计算详情")
                .build());
        }
    }

    /**
     * 从Object安全获取整数值
     */
    private int getIntValue(Object obj) {
        if (obj == null) {
            return 0;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        if (obj instanceof String) {
            try {
                return Integer.parseInt((String) obj);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }

    /**
     * 从Object安全获取BigDecimal值
     */
    private BigDecimal getBigDecimalValue(Object obj) {
        if (obj == null) {
            return BigDecimal.ZERO;
        }
        if (obj instanceof BigDecimal) {
            return (BigDecimal) obj;
        }
        if (obj instanceof Number) {
            return new BigDecimal(obj.toString());
        }
        if (obj instanceof String) {
            try {
                return new BigDecimal((String) obj);
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * 基于规则类型验证DTO字段
     * @param dto 计费规则DTO
     */
    private void validatePriceRuleDto(PriceRuleDto dto) {
        if (dto.getRuleType() == 1) { // 标准计费
            // 验证标准计费必填字段
            if (dto.getPriceUnit() == null) {
                throw BilliardsException.of(ResultCode.PARAM_ERROR, "标准计费模式下，单价不能为空");
            }
            if (dto.getPriceUnit().compareTo(BigDecimal.ZERO) <= 0) {
                throw BilliardsException.of(ResultCode.PARAM_ERROR, "单价必须大于0");
            }
            if (dto.getMinMinutes() == null) {
                throw BilliardsException.of(ResultCode.PARAM_ERROR, "标准计费模式下，最低消费时长不能为空");
            }
            if (dto.getMinMinutes() < 0) {
                throw BilliardsException.of(ResultCode.PARAM_ERROR, "最低消费时长不能为负");
            }
        } else if (dto.getRuleType() == 2) { // 阶梯计费
            // 验证阶梯计费必填字段
            if (StringUtils.isEmpty(dto.getLadderRules())) {
                throw BilliardsException.of(ResultCode.PARAM_ERROR, "阶梯计费模式下，阶梯规则不能为空");
            }

            // 验证阶梯规则的JSON格式和内容
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                List<Map<String, Object>> ladderList = objectMapper.readValue(dto.getLadderRules(), List.class);

                if (ladderList.isEmpty()) {
                    throw BilliardsException.of(ResultCode.PARAM_ERROR, "阶梯规则不能为空列表");
                }

                // 验证每个阶梯规则的有效性
                for (int i = 0; i < ladderList.size(); i++) {
                    Map<String, Object> ladder = ladderList.get(i);

                    // 检查必要字段
                    if (!ladder.containsKey("startMinute")) {
                        throw BilliardsException.of(ResultCode.PARAM_ERROR, "第" + (i + 1) + "阶梯缺少起始时间");
                    }

                    if (!ladder.containsKey("price")) {
                        throw BilliardsException.of(ResultCode.PARAM_ERROR, "第" + (i + 1) + "阶梯缺少单价");
                    }

                    // 检查价格是否大于0
                    Object priceObj = ladder.get("price");
                    double price = priceObj instanceof Number ? ((Number) priceObj).doubleValue() : 0;
                    if (price <= 0) {
                        throw BilliardsException.of(ResultCode.PARAM_ERROR, "第" + (i + 1) + "阶梯单价必须大于0");
                    }

                    // 如果不是最后一个阶梯，检查endMinute
                    if (i < ladderList.size() - 1) {
                        if (!ladder.containsKey("endMinute")) {
                            throw BilliardsException.of(ResultCode.PARAM_ERROR, "第" + (i + 1) + "阶梯缺少结束时间");
                        }
                    } else {
                        // 最后一个阶梯的endMinute应该是-1，表示无限制
                        if (ladder.containsKey("endMinute")) {
                            Object endObj = ladder.get("endMinute");
                            int endMinute = endObj instanceof Number ? ((Number) endObj).intValue() : 0;
                            if (endMinute != -1) {
                                throw BilliardsException.of(ResultCode.PARAM_ERROR, "最后一个阶梯的结束时间应为-1(无限制)");
                            }
                        }
                    }

                    // 检查阶梯的连续性（除第一个阶梯外）
                    if (i > 0) {
                        Map<String, Object> prevLadder = ladderList.get(i - 1);
                        Object prevEndObj = prevLadder.get("endMinute");
                        Object curStartObj = ladder.get("startMinute");

                        int prevEnd = prevEndObj instanceof Number ? ((Number) prevEndObj).intValue() : 0;
                        int curStart = curStartObj instanceof Number ? ((Number) curStartObj).intValue() : 0;

                        if (prevEnd != curStart) {
                            throw BilliardsException.of(ResultCode.PARAM_ERROR,
                                    "阶梯时间不连续：第" + i + "阶梯的结束时间与第" + (i + 1) + "阶梯的开始时间不一致");
                        }
                    }
                }
            } catch (JsonProcessingException e) {
                throw BilliardsException.of(ResultCode.PARAM_ERROR, "阶梯规则JSON格式无效: " + e.getMessage());
            }

            // 验证会员折扣
            if (dto.getMemberDiscount() != null) {
                if (dto.getMemberDiscount().compareTo(BigDecimal.ZERO) < 0 ||
                    dto.getMemberDiscount().compareTo(BigDecimal.ONE) > 0) {
                    throw BilliardsException.of(ResultCode.PARAM_ERROR, "会员折扣必须在0到1之间");
                }
            }
        }
    }

    /**
     * 应用计费规则到指定桌台
     * @param ruleId 计费规则ID
     * @param tableIds 桌台ID列表
     * @return 是否应用成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean applyPriceRuleToTables(String ruleId, List<String> tableIds) {
        // 参数校验
        if (tableIds == null || tableIds.isEmpty()) {
            throw BilliardsException.of(ResultCode.PARAM_ERROR, "桌台ID列表不能为空");
        }

        // 获取计费规则
        PriceRule priceRule = this.getById(ruleId);
        if (priceRule == null) {
            throw BilliardsException.of(ResultCode.NOT_FOUND, "计费规则不存在");
        }

        // 检查计费规则是否启用
        if (priceRule.getStatus() != null && priceRule.getStatus() != 0) {
            throw BilliardsException.of(ResultCode.ERROR, "计费规则已停用，无法应用");
        }

        // 直接使用自定义SQL进行批量更新
        int updatedCount = tableMapper.batchUpdatePriceRule(tableIds, ruleId);

        return updatedCount > 0;
    }

    @Override
    public BigDecimal findMinStandardPriceUnitForStore(String storeId) {
        return baseMapper.findMinStandardPriceUnitForStore(storeId);
    }
}
