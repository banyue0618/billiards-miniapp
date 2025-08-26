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
import org.dromara.billiards.domain.vo.BlsMemberBenefitVo;
import org.dromara.billiards.domain.bo.BlsMemberBenefitBo;
import org.dromara.billiards.service.IBlsMemberBenefitService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 会员权益
 *
 * @author banyue
 * @date 2025-06-17
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/memberBenefit")
public class BlsMemberBenefitController extends BaseController {

    private final IBlsMemberBenefitService blsMemberBenefitService;

    /**
     * 查询会员权益列表
     */
    @SaCheckPermission("billiards:memberBenefit:list")
    @GetMapping("/list")
    public TableDataInfo<BlsMemberBenefitVo> list(BlsMemberBenefitBo bo, PageQuery pageQuery) {
        return blsMemberBenefitService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出会员权益列表
     */
    @SaCheckPermission("billiards:memberBenefit:export")
    @Log(title = "会员权益", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsMemberBenefitBo bo, HttpServletResponse response) {
        List<BlsMemberBenefitVo> list = blsMemberBenefitService.queryList(bo);
        ExcelUtil.exportExcel(list, "会员权益", BlsMemberBenefitVo.class, response);
    }

    /**
     * 获取会员权益详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:memberBenefit:query")
    @GetMapping("/{id}")
    public R<BlsMemberBenefitVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsMemberBenefitService.queryById(id));
    }

    /**
     * 新增会员权益
     */
    @SaCheckPermission("billiards:memberBenefit:add")
    @Log(title = "会员权益", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsMemberBenefitBo bo) {
        return toAjax(blsMemberBenefitService.insertByBo(bo));
    }

    /**
     * 修改会员权益
     */
    @SaCheckPermission("billiards:memberBenefit:edit")
    @Log(title = "会员权益", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsMemberBenefitBo bo) {
        return toAjax(blsMemberBenefitService.updateByBo(bo));
    }

    /**
     * 删除会员权益
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:memberBenefit:remove")
    @Log(title = "会员权益", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsMemberBenefitService.deleteWithValidByIds(List.of(ids), true));
    }
}
