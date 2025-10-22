package org.dromara.billiards.iot.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceBindingBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceBindingVo;
import org.dromara.billiards.iot.service.IBlsIotDeviceBindingService;
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
 * 设备业务绑定（定义场景与设备动作映射）
 *
 * @author banyue
 * @date 2025-10-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/iotDeviceBinding")
public class BlsIotDeviceBindingController extends BaseController {

    private final IBlsIotDeviceBindingService blsIotDeviceBindingService;

    /**
     * 查询设备业务绑定（定义场景与设备动作映射）列表
     */
    @SaCheckPermission("billiards:iotDeviceBinding:list")
    @GetMapping("/list")
    public TableDataInfo<BlsIotDeviceBindingVo> list(BlsIotDeviceBindingBo bo, PageQuery pageQuery) {
        return blsIotDeviceBindingService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备业务绑定（定义场景与设备动作映射）列表
     */
    @SaCheckPermission("billiards:iotDeviceBinding:export")
    @Log(title = "设备业务绑定（定义场景与设备动作映射）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsIotDeviceBindingBo bo, HttpServletResponse response) {
        List<BlsIotDeviceBindingVo> list = blsIotDeviceBindingService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备业务绑定（定义场景与设备动作映射）", BlsIotDeviceBindingVo.class, response);
    }

    /**
     * 获取设备业务绑定（定义场景与设备动作映射）详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:iotDeviceBinding:query")
    @GetMapping("/{id}")
    public R<BlsIotDeviceBindingVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(blsIotDeviceBindingService.queryById(id));
    }

    /**
     * 新增设备业务绑定（定义场景与设备动作映射）
     */
    @SaCheckPermission("billiards:iotDeviceBinding:add")
    @Log(title = "设备业务绑定（定义场景与设备动作映射）", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsIotDeviceBindingBo bo) {
        return toAjax(blsIotDeviceBindingService.insertByBo(bo));
    }

    /**
     * 修改设备业务绑定（定义场景与设备动作映射）
     */
    @SaCheckPermission("billiards:iotDeviceBinding:edit")
    @Log(title = "设备业务绑定（定义场景与设备动作映射）", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsIotDeviceBindingBo bo) {
        return toAjax(blsIotDeviceBindingService.updateByBo(bo));
    }

    /**
     * 删除设备业务绑定（定义场景与设备动作映射）
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:iotDeviceBinding:remove")
    @Log(title = "设备业务绑定（定义场景与设备动作映射）", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(blsIotDeviceBindingService.deleteWithValidByIds(List.of(ids), true));
    }
}
