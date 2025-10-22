package org.dromara.billiards.iot.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceVo;
import org.dromara.billiards.iot.service.IBlsIotDeviceService;
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
 * IoT设备
 *
 * @author banyue
 * @date 2025-10-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/iotDevice")
public class BlsIotDeviceController extends BaseController {

    private final IBlsIotDeviceService blsIotDeviceService;

    /**
     * 查询IoT设备列表
     */
    @SaCheckPermission("billiards:iotDevice:list")
    @GetMapping("/list")
    public TableDataInfo<BlsIotDeviceVo> list(BlsIotDeviceBo bo, PageQuery pageQuery) {
        return blsIotDeviceService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出IoT设备列表
     */
    @SaCheckPermission("billiards:iotDevice:export")
    @Log(title = "IoT设备", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsIotDeviceBo bo, HttpServletResponse response) {
        List<BlsIotDeviceVo> list = blsIotDeviceService.queryList(bo);
        ExcelUtil.exportExcel(list, "IoT设备", BlsIotDeviceVo.class, response);
    }

    /**
     * 获取IoT设备详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:iotDevice:query")
    @GetMapping("/{id}")
    public R<BlsIotDeviceVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(blsIotDeviceService.queryById(id));
    }

    /**
     * 新增IoT设备
     */
    @SaCheckPermission("billiards:iotDevice:add")
    @Log(title = "IoT设备", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsIotDeviceBo bo) {
        return toAjax(blsIotDeviceService.insertByBo(bo));
    }

    /**
     * 修改IoT设备
     */
    @SaCheckPermission("billiards:iotDevice:edit")
    @Log(title = "IoT设备", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsIotDeviceBo bo) {
        return toAjax(blsIotDeviceService.updateByBo(bo));
    }

    /**
     * 删除IoT设备
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:iotDevice:remove")
    @Log(title = "IoT设备", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(blsIotDeviceService.deleteWithValidByIds(List.of(ids), true));
    }
}
