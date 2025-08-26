package org.dromara.billiards.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.billiards.domain.vo.BlsPointsRuleVo;
import org.dromara.billiards.domain.bo.BlsPointsRuleBo;
import org.dromara.billiards.service.IBlsPointsRuleService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 积分规则
 *
 * @author banyue
 * @date 2025-06-17
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/pointsRule")
public class BlsPointsRuleController extends BaseController {

    private final IBlsPointsRuleService blsPointsRuleService;

    /**
     * 查询积分规则列表
     */
    @SaCheckPermission("billiards:pointsRule:list")
    @GetMapping("/list")
    public TableDataInfo<BlsPointsRuleVo> list(BlsPointsRuleBo bo, PageQuery pageQuery) {
        return blsPointsRuleService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出积分规则列表
     */
    @SaCheckPermission("billiards:pointsRule:export")
    @Log(title = "积分规则", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsPointsRuleBo bo, HttpServletResponse response) {
        List<BlsPointsRuleVo> list = blsPointsRuleService.queryList(bo);
        ExcelUtil.exportExcel(list, "积分规则", BlsPointsRuleVo.class, response);
    }

    /**
     * 获取积分规则详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:pointsRule:query")
    @GetMapping("/{id}")
    public R<BlsPointsRuleVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsPointsRuleService.queryById(id));
    }

    /**
     * 新增积分规则
     */
    @SaCheckPermission("billiards:pointsRule:add")
    @Log(title = "积分规则", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsPointsRuleBo bo) {
        return toAjax(blsPointsRuleService.insertByBo(bo));
    }

    /**
     * 修改积分规则
     */
    @SaCheckPermission("billiards:pointsRule:edit")
    @Log(title = "积分规则", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsPointsRuleBo bo) {
        return toAjax(blsPointsRuleService.updateByBo(bo));
    }

    /**
     * 删除积分规则
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:pointsRule:remove")
    @Log(title = "积分规则", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsPointsRuleService.deleteWithValidByIds(List.of(ids), true));
    }
}
