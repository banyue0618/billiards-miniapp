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
import org.dromara.billiards.domain.vo.BlsMemberPointsRecordVo;
import org.dromara.billiards.domain.bo.BlsMemberPointsRecordBo;
import org.dromara.billiards.service.IBlsMemberPointsRecordService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 会员积分记录
 *
 * @author banyue
 * @date 2025-06-17
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/memberPointsRecord")
public class BlsMemberPointsRecordController extends BaseController {

    private final IBlsMemberPointsRecordService blsMemberPointsRecordService;

    /**
     * 查询会员积分记录列表
     */
    @SaCheckPermission("billiards:memberPointsRecord:list")
    @GetMapping("/list")
    public TableDataInfo<BlsMemberPointsRecordVo> list(BlsMemberPointsRecordBo bo, PageQuery pageQuery) {
        return blsMemberPointsRecordService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出会员积分记录列表
     */
    @SaCheckPermission("billiards:memberPointsRecord:export")
    @Log(title = "会员积分记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsMemberPointsRecordBo bo, HttpServletResponse response) {
        List<BlsMemberPointsRecordVo> list = blsMemberPointsRecordService.queryList(bo);
        ExcelUtil.exportExcel(list, "会员积分记录", BlsMemberPointsRecordVo.class, response);
    }

    /**
     * 获取会员积分记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:memberPointsRecord:query")
    @GetMapping("/{id}")
    public R<BlsMemberPointsRecordVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsMemberPointsRecordService.queryById(id));
    }

    /**
     * 新增会员积分记录
     */
    @SaCheckPermission("billiards:memberPointsRecord:add")
    @Log(title = "会员积分记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsMemberPointsRecordBo bo) {
        return toAjax(blsMemberPointsRecordService.insertByBo(bo));
    }

    /**
     * 修改会员积分记录
     */
    @SaCheckPermission("billiards:memberPointsRecord:edit")
    @Log(title = "会员积分记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsMemberPointsRecordBo bo) {
        return toAjax(blsMemberPointsRecordService.updateByBo(bo));
    }

    /**
     * 删除会员积分记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:memberPointsRecord:remove")
    @Log(title = "会员积分记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsMemberPointsRecordService.deleteWithValidByIds(List.of(ids), true));
    }
}
