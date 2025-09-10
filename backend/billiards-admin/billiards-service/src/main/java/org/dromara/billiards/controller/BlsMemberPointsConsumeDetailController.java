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
import org.dromara.billiards.domain.vo.BlsMemberPointsConsumeDetailVo;
import org.dromara.billiards.domain.bo.BlsMemberPointsConsumeDetailBo;
import org.dromara.billiards.service.IBlsMemberPointsConsumeDetailService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 会员积分消费详情
 *
 * @author banyue
 * @date 2025-09-09
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/memberPointsConsumeDetail")
public class BlsMemberPointsConsumeDetailController extends BaseController {

    private final IBlsMemberPointsConsumeDetailService blsMemberPointsConsumeDetailService;

    /**
     * 查询会员积分消费详情列表
     */
    @SaCheckPermission("billiards:memberPointsConsumeDetail:list")
    @GetMapping("/list")
    public TableDataInfo<BlsMemberPointsConsumeDetailVo> list(BlsMemberPointsConsumeDetailBo bo, PageQuery pageQuery) {
        return blsMemberPointsConsumeDetailService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出会员积分消费详情列表
     */
    @SaCheckPermission("billiards:memberPointsConsumeDetail:export")
    @Log(title = "会员积分消费详情", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsMemberPointsConsumeDetailBo bo, HttpServletResponse response) {
        List<BlsMemberPointsConsumeDetailVo> list = blsMemberPointsConsumeDetailService.queryList(bo);
        ExcelUtil.exportExcel(list, "会员积分消费详情", BlsMemberPointsConsumeDetailVo.class, response);
    }

    /**
     * 获取会员积分消费详情详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:memberPointsConsumeDetail:query")
    @GetMapping("/{id}")
    public R<BlsMemberPointsConsumeDetailVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsMemberPointsConsumeDetailService.queryById(id));
    }

    /**
     * 新增会员积分消费详情
     */
    @SaCheckPermission("billiards:memberPointsConsumeDetail:add")
    @Log(title = "会员积分消费详情", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsMemberPointsConsumeDetailBo bo) {
        return toAjax(blsMemberPointsConsumeDetailService.insertByBo(bo));
    }

    /**
     * 修改会员积分消费详情
     */
    @SaCheckPermission("billiards:memberPointsConsumeDetail:edit")
    @Log(title = "会员积分消费详情", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsMemberPointsConsumeDetailBo bo) {
        return toAjax(blsMemberPointsConsumeDetailService.updateByBo(bo));
    }

    /**
     * 删除会员积分消费详情
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:memberPointsConsumeDetail:remove")
    @Log(title = "会员积分消费详情", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsMemberPointsConsumeDetailService.deleteWithValidByIds(List.of(ids), true));
    }
}
