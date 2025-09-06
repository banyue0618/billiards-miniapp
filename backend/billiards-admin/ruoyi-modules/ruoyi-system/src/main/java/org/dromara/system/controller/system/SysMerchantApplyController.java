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
import org.dromara.system.domain.vo.SysMerchantApplyVo;
import org.dromara.system.domain.bo.SysMerchantApplyBo;
import org.dromara.system.service.ISysMerchantApplyService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 商户注册申请
 *
 * @author banyue
 * @date 2025-09-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/merchantApply")
public class SysMerchantApplyController extends BaseController {

    private final ISysMerchantApplyService sysMerchantApplyService;

    /**
     * 查询商户注册申请列表
     */
    @SaCheckPermission("system:merchantApply:list")
    @GetMapping("/list")
    public TableDataInfo<SysMerchantApplyVo> list(SysMerchantApplyBo bo, PageQuery pageQuery) {
        return sysMerchantApplyService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商户注册申请列表
     */
    @SaCheckPermission("system:merchantApply:export")
    @Log(title = "商户注册申请", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysMerchantApplyBo bo, HttpServletResponse response) {
        List<SysMerchantApplyVo> list = sysMerchantApplyService.queryList(bo);
        ExcelUtil.exportExcel(list, "商户注册申请", SysMerchantApplyVo.class, response);
    }

    /**
     * 获取商户注册申请详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:merchantApply:query")
    @GetMapping("/{id}")
    public R<SysMerchantApplyVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sysMerchantApplyService.queryById(id));
    }

    /**
     * 新增商户注册申请
     */
    @SaCheckPermission("system:merchantApply:add")
    @Log(title = "商户注册申请", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysMerchantApplyBo bo) {
        return toAjax(sysMerchantApplyService.insertByBo(bo));
    }

    /**
     * 修改商户注册申请
     */
    @SaCheckPermission("system:merchantApply:edit")
    @Log(title = "商户注册申请", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysMerchantApplyBo bo) {
        return toAjax(sysMerchantApplyService.updateByBo(bo));
    }

    /**
     * 删除商户注册申请
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:merchantApply:remove")
    @Log(title = "商户注册申请", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysMerchantApplyService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 审批通过
     */
    @SaCheckPermission("system:merchantApply:audit")
    @PostMapping("/approve/{id}")
    public R<Void> approve(@PathVariable Long id, @RequestBody(required = false) SysMerchantApplyBo bo) {
        sysMerchantApplyService.approve(id, bo);
        return R.ok();
    }

    /**
     * 审批驳回
     */
    @SaCheckPermission("system:merchantApply:audit")
    @PostMapping("/reject/{id}")
    public R<Void> reject(@PathVariable Long id, @RequestBody SysMerchantApplyBo bo) {
        sysMerchantApplyService.reject(id, bo.getAuditReason());
        return R.ok();
    }
}
