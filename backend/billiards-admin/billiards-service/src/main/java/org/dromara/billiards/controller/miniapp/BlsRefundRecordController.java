package org.dromara.billiards.controller.miniapp;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.billiards.domain.bo.BlsRefundRecordBo;
import org.dromara.billiards.domain.vo.BlsRefundRecordVo;
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
import org.dromara.billiards.service.IBlsRefundRecordService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 退款记录
 *
 * @author banyue
 * @date 2025-06-08
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/refundRecord")
public class BlsRefundRecordController extends BaseController {

    private final IBlsRefundRecordService blsRefundRecordService;

    /**
     * 查询退款记录列表
     */
    @SaCheckPermission("billiards:refundRecord:list")
    @GetMapping("/list")
    public TableDataInfo<BlsRefundRecordVo> list(BlsRefundRecordBo bo, PageQuery pageQuery) {
        return blsRefundRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出退款记录列表
     */
    @SaCheckPermission("billiards:refundRecord:export")
    @Log(title = "退款记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsRefundRecordBo bo, HttpServletResponse response) {
        List<BlsRefundRecordVo> list = blsRefundRecordService.queryList(bo);
        ExcelUtil.exportExcel(list, "退款记录", BlsRefundRecordVo.class, response);
    }

    /**
     * 获取退款记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:refundRecord:query")
    @GetMapping("/{id}")
    public R<BlsRefundRecordVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsRefundRecordService.queryById(id));
    }

    /**
     * 新增退款记录
     */
    @SaCheckPermission("billiards:refundRecord:add")
    @Log(title = "退款记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsRefundRecordBo bo) {
        return toAjax(blsRefundRecordService.insertByBo(bo));
    }

    /**
     * 修改退款记录
     */
    @SaCheckPermission("billiards:refundRecord:edit")
    @Log(title = "退款记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsRefundRecordBo bo) {
        return toAjax(blsRefundRecordService.updateByBo(bo));
    }

    /**
     * 删除退款记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:refundRecord:remove")
    @Log(title = "退款记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsRefundRecordService.deleteWithValidByIds(List.of(ids), true));
    }
}
