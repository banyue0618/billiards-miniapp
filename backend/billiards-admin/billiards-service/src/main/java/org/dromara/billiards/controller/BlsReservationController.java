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
import org.dromara.billiards.domain.vo.BlsReservationVo;
import org.dromara.billiards.domain.bo.BlsReservationBo;
import org.dromara.billiards.service.IBlsReservationService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 用户预约记录
 *
 * @author banyue
 * @date 2025-11-03
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/reservation")
public class BlsReservationController extends BaseController {

    private final IBlsReservationService blsReservationService;

    /**
     * 查询用户预约记录列表
     */
    @SaCheckPermission("billiards:reservation:list")
    @GetMapping("/list")
    public TableDataInfo<BlsReservationVo> list(BlsReservationBo bo, PageQuery pageQuery) {
        return blsReservationService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出用户预约记录列表
     */
    @SaCheckPermission("billiards:reservation:export")
    @Log(title = "用户预约记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsReservationBo bo, HttpServletResponse response) {
        List<BlsReservationVo> list = blsReservationService.queryList(bo);
        ExcelUtil.exportExcel(list, "用户预约记录", BlsReservationVo.class, response);
    }

    /**
     * 获取用户预约记录详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:reservation:query")
    @GetMapping("/{id}")
    public R<BlsReservationVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(blsReservationService.queryById(id));
    }

    /**
     * 新增用户预约记录
     */
    @SaCheckPermission("billiards:reservation:add")
    @Log(title = "用户预约记录", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsReservationBo bo) {
        return toAjax(blsReservationService.insertByBo(bo));
    }

    /**
     * 修改用户预约记录
     */
    @SaCheckPermission("billiards:reservation:edit")
    @Log(title = "用户预约记录", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsReservationBo bo) {
        return toAjax(blsReservationService.updateByBo(bo));
    }

    /**
     * 删除用户预约记录
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:reservation:remove")
    @Log(title = "用户预约记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(blsReservationService.deleteWithValidByIds(List.of(ids), true));
    }

    @Log(title = "用户预约", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping("/reserve")
    public R<BlsReservationVo> reserve(@Validated(AddGroup.class) @RequestBody BlsReservationBo bo) {
        return R.ok(blsReservationService.reserve(bo));
    }
}
