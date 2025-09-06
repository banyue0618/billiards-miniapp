package org.dromara.billiards.controller.admin;

import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.convert.PriceRuleConvert;
import org.dromara.billiards.domain.bo.PriceRuleDto;
import org.dromara.billiards.domain.bo.PriceRulePreviewDto;
import org.dromara.billiards.domain.entity.BlsPriceRule;
import org.dromara.billiards.domain.vo.PriceRulePreviewVO;
import org.dromara.billiards.domain.vo.PriceRuleVO;
import org.dromara.billiards.service.PriceRuleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 计费规则管理控制器
 */
@Slf4j
@RestController("adminPriceRuleController")
@RequestMapping("/api/admin/priceRules")
@RequiredArgsConstructor
@Tag(name = "计费规则接口", description = "管理端计费规则相关接口")
public class PriceRuleController {

    private final PriceRuleService priceRuleService;
    private final PriceRuleConvert priceRuleConvert = PriceRuleConvert.INSTANCE;

    /**
     * 获取计费规则详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "计费规则详情", description = "获取计费规则详细信息")
    public R<PriceRuleVO> detail(@PathVariable String id) {
        BlsPriceRule blsPriceRule = priceRuleService.getPriceRuleInfo(id);
        return ApiResult.success(priceRuleConvert.toVo(blsPriceRule));
    }

    /**
     * 新增计费规则
     */
    @PostMapping
    @Operation(summary = "新增计费规则", description = "添加新的计费规则")
    public R<PriceRuleVO> add(@RequestBody PriceRuleDto dto) {
        BlsPriceRule blsPriceRule = priceRuleService.createPriceRule(dto);
        return ApiResult.success(priceRuleConvert.toVo(blsPriceRule));
    }

    /**
     * 修改计费规则
     */
    @PutMapping("/{id}")
    @Operation(summary = "修改计费规则", description = "修改计费规则信息")
    public R<PriceRuleVO> update(@PathVariable String id, @RequestBody PriceRuleDto dto) {
        BlsPriceRule updatedBlsPriceRule = priceRuleService.updatePriceRule(id, dto);
        return ApiResult.success(priceRuleConvert.toVo(updatedBlsPriceRule));
    }

    /**
     * 删除计费规则
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除计费规则", description = "删除指定计费规则")
    public R<Boolean> delete(@PathVariable String id) {
        // Service layer handles existence check and throws exception on failure
        priceRuleService.deletePriceRule(id);
        return ApiResult.success(true);
    }

    /**
     * 启用/禁用计费规则
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "修改状态", description = "启用或禁用计费规则")
    public R<Boolean> updateStatus(@PathVariable String id, @RequestParam Integer status) {
        priceRuleService.updatePriceRuleStatus(id, status);
        return ApiResult.success(true);
    }

    /**
     * 获取商家的所有计费规则 (非分页)
     */
    @GetMapping("/list")
    @Operation(summary = "获取商家所有计费规则", description = "获取指定商家的所有计费规则（不分页）")
    public R<List<PriceRuleVO>> listByMerchant() {
        List<BlsPriceRule> ruleList = priceRuleService.listPriceRules();
        return ApiResult.success(ruleList.stream().map(priceRuleConvert::toVo).toList());
    }

    /**
     * 预览计费规则计算结果
     */
    @PostMapping("/preview")
    @Operation(summary = "预览计费规则", description = "根据计费规则预览计费结果")
    public R<PriceRulePreviewVO> previewPriceRule(@RequestBody PriceRulePreviewDto previewDto) {
        PriceRulePreviewVO previewResult = priceRuleService.previewPriceCalculation(
            previewDto.getRuleId(),
            previewDto.getMinutes(),
            previewDto.getIsMember()
        );
        return ApiResult.success(previewResult);
    }

    /**
     * 应用计费规则到指定桌台
     */
    @PostMapping("/{id}/apply")
    @Operation(summary = "应用计费规则", description = "将计费规则应用到指定桌台")
    public R<Boolean> applyToTables(@PathVariable String id, @RequestBody List<String> tableIds) {
        boolean result = priceRuleService.applyPriceRuleToTables(id, tableIds);
        return ApiResult.success(result);
    }
}
