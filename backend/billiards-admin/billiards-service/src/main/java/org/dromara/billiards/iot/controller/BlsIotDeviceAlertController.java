package org.dromara.billiards.iot.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceAlertBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceAlertVo;
import org.dromara.billiards.iot.service.IBlsIotDeviceAlertService;
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
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 设备告警（记录设备异常信息）
 *
 * @author banyue
 * @date 2025-10-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/iotDeviceAlert")
public class BlsIotDeviceAlertController extends BaseController {

    private final IBlsIotDeviceAlertService blsIotDeviceAlertService;

    /**
     * 查询设备告警（记录设备异常信息）列表
     */
    @SaCheckPermission("billiards:iotDeviceAlert:list")
    @GetMapping("/list")
    public TableDataInfo<BlsIotDeviceAlertVo> list(BlsIotDeviceAlertBo bo, PageQuery pageQuery) {
        return blsIotDeviceAlertService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备告警（记录设备异常信息）列表
     */
    @SaCheckPermission("billiards:iotDeviceAlert:export")
    @Log(title = "设备告警（记录设备异常信息）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsIotDeviceAlertBo bo, HttpServletResponse response) {
        List<BlsIotDeviceAlertVo> list = blsIotDeviceAlertService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备告警（记录设备异常信息）", BlsIotDeviceAlertVo.class, response);
    }

    /**
     * 获取设备告警（记录设备异常信息）详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:iotDeviceAlert:query")
    @GetMapping("/{id}")
    public R<BlsIotDeviceAlertVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(blsIotDeviceAlertService.queryById(id));
    }

    /**
     * 新增设备告警（记录设备异常信息）
     */
    @SaCheckPermission("billiards:iotDeviceAlert:add")
    @Log(title = "设备告警（记录设备异常信息）", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsIotDeviceAlertBo bo) {
        return toAjax(blsIotDeviceAlertService.insertByBo(bo));
    }

    /**
     * 修改设备告警（记录设备异常信息）
     */
    @SaCheckPermission("billiards:iotDeviceAlert:edit")
    @Log(title = "设备告警（记录设备异常信息）", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsIotDeviceAlertBo bo) {
        return toAjax(blsIotDeviceAlertService.updateByBo(bo));
    }

    /**
     * 删除设备告警（记录设备异常信息）
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:iotDeviceAlert:remove")
    @Log(title = "设备告警（记录设备异常信息）", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(blsIotDeviceAlertService.deleteWithValidByIds(List.of(ids), true));
    }
}
