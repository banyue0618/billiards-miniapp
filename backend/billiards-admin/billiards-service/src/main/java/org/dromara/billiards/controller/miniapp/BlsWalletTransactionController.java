package org.dromara.billiards.controller.miniapp;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.billiards.domain.bo.BlsWalletTransactionBo;
import org.dromara.billiards.domain.vo.BlsWalletTransactionVo;
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
import org.dromara.billiards.service.IBlsWalletTransactionService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 用户钱包流水
 *
 * @author banyue
 * @date 2025-06-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/walletTransaction")
public class BlsWalletTransactionController extends BaseController {

    private final IBlsWalletTransactionService blsWalletTransactionService;

    /**
     * 查询用户钱包流水列表
     */
    @SaCheckPermission("billiards:walletTransaction:list")
    @GetMapping("/list")
    public TableDataInfo<BlsWalletTransactionVo> list(BlsWalletTransactionBo bo, PageQuery pageQuery) {
        return blsWalletTransactionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户钱包流水列表
     */
    @SaCheckPermission("billiards:walletTransaction:export")
    @Log(title = "用户钱包流水", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsWalletTransactionBo bo, HttpServletResponse response) {
        List<BlsWalletTransactionVo> list = blsWalletTransactionService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户钱包流水", BlsWalletTransactionVo.class, response);
    }

    /**
     * 获取用户钱包流水详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:walletTransaction:query")
    @GetMapping("/{id}")
    public R<BlsWalletTransactionVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsWalletTransactionService.queryById(id));
    }

    /**
     * 新增用户钱包流水
     */
    @SaCheckPermission("billiards:walletTransaction:add")
    @Log(title = "用户钱包流水", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsWalletTransactionBo bo) {
        return toAjax(blsWalletTransactionService.insertByBo(bo));
    }

    /**
     * 修改用户钱包流水
     */
    @SaCheckPermission("billiards:walletTransaction:edit")
    @Log(title = "用户钱包流水", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsWalletTransactionBo bo) {
        return toAjax(blsWalletTransactionService.updateByBo(bo));
    }

    /**
     * 删除用户钱包流水
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:walletTransaction:remove")
    @Log(title = "用户钱包流水", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsWalletTransactionService.deleteWithValidByIds(List.of(ids), true));
    }
}
