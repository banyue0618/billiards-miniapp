package org.dromara.billiards.iot.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.dromara.billiards.iot.domain.bo.BlsIotControlLogBo;
import org.dromara.billiards.iot.domain.vo.BlsIotControlLogVo;
import org.dromara.billiards.iot.service.IBlsIotControlLogService;
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
 * 设备控制日志（记录执行命令历史）
 *
 * @author banyue
 * @date 2025-10-16
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/iotControlLog")
public class BlsIotControlLogController extends BaseController {

    private final IBlsIotControlLogService blsIotControlLogService;

    /**
     * 查询设备控制日志（记录执行命令历史）列表
     */
    @SaCheckPermission("billiards:iotControlLog:list")
    @GetMapping("/list")
    public TableDataInfo<BlsIotControlLogVo> list(BlsIotControlLogBo bo, PageQuery pageQuery) {
        return blsIotControlLogService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出设备控制日志（记录执行命令历史）列表
     */
    @SaCheckPermission("billiards:iotControlLog:export")
    @Log(title = "设备控制日志（记录执行命令历史）", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsIotControlLogBo bo, HttpServletResponse response) {
        List<BlsIotControlLogVo> list = blsIotControlLogService.queryList(bo);
        ExcelUtil.exportExcel(list, "设备控制日志（记录执行命令历史）", BlsIotControlLogVo.class, response);
    }

    /**
     * 获取设备控制日志（记录执行命令历史）详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:iotControlLog:query")
    @GetMapping("/{id}")
    public R<BlsIotControlLogVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(blsIotControlLogService.queryById(id));
    }

    /**
     * 新增设备控制日志（记录执行命令历史）
     */
    @SaCheckPermission("billiards:iotControlLog:add")
    @Log(title = "设备控制日志（记录执行命令历史）", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsIotControlLogBo bo) {
        return toAjax(blsIotControlLogService.insertByBo(bo));
    }

    /**
     * 修改设备控制日志（记录执行命令历史）
     */
    @SaCheckPermission("billiards:iotControlLog:edit")
    @Log(title = "设备控制日志（记录执行命令历史）", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsIotControlLogBo bo) {
        return toAjax(blsIotControlLogService.updateByBo(bo));
    }

    /**
     * 删除设备控制日志（记录执行命令历史）
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:iotControlLog:remove")
    @Log(title = "设备控制日志（记录执行命令历史）", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(blsIotControlLogService.deleteWithValidByIds(List.of(ids), true));
    }
}
