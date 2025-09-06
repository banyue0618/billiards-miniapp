package org.dromara.system.controller.system;

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
import org.dromara.system.domain.vo.SysTenantApplyVo;
import org.dromara.system.domain.bo.SysTenantApplyBo;
import org.dromara.system.service.ISysTenantApplyService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 租户注册申请
 *
 * @author banyue
 * @date 2025-09-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/tenantApply")
public class SysTenantApplyController extends BaseController {

    private final ISysTenantApplyService sysTenantApplyService;

    /**
     * 查询租户注册申请列表
     */
    @SaCheckPermission("system:tenantApply:list")
    @GetMapping("/list")
    public TableDataInfo<SysTenantApplyVo> list(SysTenantApplyBo bo, PageQuery pageQuery) {
        return sysTenantApplyService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出租户注册申请列表
     */
    @SaCheckPermission("system:tenantApply:export")
    @Log(title = "租户注册申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysTenantApplyBo bo, HttpServletResponse response) {
        List<SysTenantApplyVo> list = sysTenantApplyService.queryList(bo);
        ExcelUtil.exportExcel(list, "租户注册申请", SysTenantApplyVo.class, response);
    }

    /**
     * 获取租户注册申请详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:tenantApply:query")
    @GetMapping("/{id}")
    public R<SysTenantApplyVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sysTenantApplyService.queryById(id));
    }

    /**
     * 新增租户注册申请
     */
    @SaCheckPermission("system:tenantApply:add")
    @Log(title = "租户注册申请", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysTenantApplyBo bo) {
        return toAjax(sysTenantApplyService.insertByBo(bo));
    }

    /**
     * 修改租户注册申请
     */
    @SaCheckPermission("system:tenantApply:edit")
    @Log(title = "租户注册申请", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysTenantApplyBo bo) {
        return toAjax(sysTenantApplyService.updateByBo(bo));
    }

    /**
     * 删除租户注册申请
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:tenantApply:remove")
    @Log(title = "租户注册申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysTenantApplyService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 审批通过
     */
    @SaCheckPermission("system:tenantApply:audit")
    @PostMapping("/approve/{id}")
    public R<Void> approve(@PathVariable Long id, @RequestBody(required = false) SysTenantApplyBo bo) {
        sysTenantApplyService.approve(id, bo);
        return R.ok();
    }

    /**
     * 审批驳回
     */
    @SaCheckPermission("system:tenantApply:audit")
    @PostMapping("/reject/{id}")
    public R<Void> reject(@PathVariable Long id, @RequestBody SysTenantApplyBo bo) {
        sysTenantApplyService.reject(id, bo.getAuditReason());
        return R.ok();
    }
}
