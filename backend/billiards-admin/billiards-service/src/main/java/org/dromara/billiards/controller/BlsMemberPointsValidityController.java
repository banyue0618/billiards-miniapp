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
import org.dromara.billiards.domain.vo.BlsMemberPointsValidityVo;
import org.dromara.billiards.domain.bo.BlsMemberPointsValidityBo;
import org.dromara.billiards.service.IBlsMemberPointsValidityService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 积分有效期
 *
 * @author banyue
 * @date 2025-06-17
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/memberPointsValidity")
public class BlsMemberPointsValidityController extends BaseController {

    private final IBlsMemberPointsValidityService blsMemberPointsValidityService;

    /**
     * 查询积分有效期列表
     */
    @SaCheckPermission("billiards:memberPointsValidity:list")
    @GetMapping("/list")
    public TableDataInfo<BlsMemberPointsValidityVo> list(BlsMemberPointsValidityBo bo, PageQuery pageQuery) {
        return blsMemberPointsValidityService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出积分有效期列表
     */
    @SaCheckPermission("billiards:memberPointsValidity:export")
    @Log(title = "积分有效期", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsMemberPointsValidityBo bo, HttpServletResponse response) {
        List<BlsMemberPointsValidityVo> list = blsMemberPointsValidityService.queryList(bo);
        ExcelUtil.exportExcel(list, "积分有效期", BlsMemberPointsValidityVo.class, response);
    }

    /**
     * 获取积分有效期详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:memberPointsValidity:query")
    @GetMapping("/{id}")
    public R<BlsMemberPointsValidityVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsMemberPointsValidityService.queryById(id));
    }

    /**
     * 新增积分有效期
     */
    @SaCheckPermission("billiards:memberPointsValidity:add")
    @Log(title = "积分有效期", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsMemberPointsValidityBo bo) {
        return toAjax(blsMemberPointsValidityService.insertByBo(bo));
    }

    /**
     * 修改积分有效期
     */
    @SaCheckPermission("billiards:memberPointsValidity:edit")
    @Log(title = "积分有效期", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsMemberPointsValidityBo bo) {
        return toAjax(blsMemberPointsValidityService.updateByBo(bo));
    }

    /**
     * 删除积分有效期
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:memberPointsValidity:remove")
    @Log(title = "积分有效期", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsMemberPointsValidityService.deleteWithValidByIds(List.of(ids), true));
    }
}
