package org.dromara.billiards.controller.miniapp;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.billiards.domain.bo.BlsWalletAccountBo;
import org.dromara.billiards.domain.vo.BlsWalletAccountVo;
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
import org.dromara.billiards.service.IBlsWalletAccountService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 用户钱包账户
 *
 * @author banyue
 * @date 2025-06-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/walletAccount")
public class BlsWalletAccountController extends BaseController {

    private final IBlsWalletAccountService blsWalletAccountService;

    /**
     * 查询用户钱包账户列表
     */
    @SaCheckPermission("billiards:walletAccount:list")
    @GetMapping("/list")
    public TableDataInfo<BlsWalletAccountVo> list(BlsWalletAccountBo bo, PageQuery pageQuery) {
        return blsWalletAccountService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户钱包账户列表
     */
    @SaCheckPermission("billiards:walletAccount:export")
    @Log(title = "用户钱包账户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsWalletAccountBo bo, HttpServletResponse response) {
        List<BlsWalletAccountVo> list = blsWalletAccountService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户钱包账户", BlsWalletAccountVo.class, response);
    }

    /**
     * 获取用户钱包账户详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:walletAccount:query")
    @GetMapping("/{id}")
    public R<BlsWalletAccountVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsWalletAccountService.queryById(id));
    }

    /**
     * 新增用户钱包账户
     */
    @SaCheckPermission("billiards:walletAccount:add")
    @Log(title = "用户钱包账户", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsWalletAccountBo bo) {
        return toAjax(blsWalletAccountService.insertByBo(bo));
    }

    /**
     * 修改用户钱包账户
     */
    @SaCheckPermission("billiards:walletAccount:edit")
    @Log(title = "用户钱包账户", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsWalletAccountBo bo) {
        return toAjax(blsWalletAccountService.updateByBo(bo));
    }

    /**
     * 删除用户钱包账户
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:walletAccount:remove")
    @Log(title = "用户钱包账户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsWalletAccountService.deleteWithValidByIds(List.of(ids), true));
    }
}
