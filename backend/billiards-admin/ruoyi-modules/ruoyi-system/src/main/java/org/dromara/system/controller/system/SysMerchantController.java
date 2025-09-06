package org.dromara.system.controller.system;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.common.tenant.helper.MerchantHolder;
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
import org.dromara.system.domain.vo.SysMerchantVo;
import org.dromara.system.domain.bo.SysMerchantBo;
import org.dromara.system.service.ISysMerchantService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.core.constant.TenantConstants;
import cn.dev33.satoken.annotation.SaCheckRole;

/**
 * 商户
 *
 * @author banyue
 * @date 2025-09-01
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/merchant")
public class SysMerchantController extends BaseController {

    private final ISysMerchantService sysMerchantService;

    /**
     * 查询商户列表
     */
    @SaCheckPermission("system:merchant:list")
    @GetMapping("/list")
    public TableDataInfo<SysMerchantVo> list(SysMerchantBo bo, PageQuery pageQuery) {
        return sysMerchantService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出商户列表
     */
    @SaCheckPermission("system:merchant:export")
    @Log(title = "商户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(SysMerchantBo bo, HttpServletResponse response) {
        List<SysMerchantVo> list = sysMerchantService.queryList(bo);
        ExcelUtil.exportExcel(list, "商户", SysMerchantVo.class, response);
    }

    /**
     * 获取商户详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("system:merchant:query")
    @GetMapping("/{id}")
    public R<SysMerchantVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(sysMerchantService.queryById(id));
    }

    /**
     * 新增商户
     */
    @SaCheckPermission("system:merchant:add")
    @Log(title = "商户", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody SysMerchantBo bo) {
        return toAjax(sysMerchantService.insertByBo(bo));
    }

    /**
     * 修改商户
     */
    @SaCheckPermission("system:merchant:edit")
    @Log(title = "商户", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody SysMerchantBo bo) {
        return toAjax(sysMerchantService.updateByBo(bo));
    }

    /**
     * 删除商户
     *
     * @param ids 主键串
     */
    @SaCheckPermission("system:merchant:remove")
    @Log(title = "商户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(sysMerchantService.deleteWithValidByIds(List.of(ids), true));
    }

    /**
     * 动态切换商户（会话级）
     */
    @SaCheckPermission("system:merchant:list")
    @PostMapping("/dynamic/{merchantId}")
    public R<Void> dynamicMerchant(@NotBlank(message = "商户ID不能为空") @PathVariable String merchantId) {
        MerchantHolder.set(merchantId);
        return R.ok();
    }

    /**
     * 清除动态商户
     */
    @SaCheckPermission("system:merchant:list")
    @PostMapping("/dynamic/clear")
    public R<Void> dynamicMerchantClear() {
        MerchantHolder.clearSession();
        MerchantHolder.clearThread();
        return R.ok();
    }

    /**
     * 获取当前租户下的全部商户列表（无分页）
     */
    @SaCheckPermission("system:merchant:list")
    @GetMapping("/listAllByCurrentTenant")
    public R<List<SysMerchantVo>> listAllByCurrentTenant(SysMerchantBo bo) {
        // SysMerchant 继承 TenantEntity，MyBatis-Plus 全局租户拦截生效，自动按当前租户过滤
        List<SysMerchantVo> list = sysMerchantService.queryList(bo);
        return R.ok(list);
    }
}
