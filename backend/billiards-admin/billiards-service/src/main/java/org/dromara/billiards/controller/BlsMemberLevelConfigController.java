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
import org.dromara.billiards.domain.vo.BlsMemberLevelConfigVo;
import org.dromara.billiards.domain.bo.BlsMemberLevelConfigBo;
import org.dromara.billiards.service.IBlsMemberLevelConfigService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 会员等级配置
 *
 * @author banyue
 * @date 2025-06-17
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/memberLevelConfig")
public class BlsMemberLevelConfigController extends BaseController {

    private final IBlsMemberLevelConfigService blsMemberLevelConfigService;

    /**
     * 查询会员等级配置列表
     */
    @SaCheckPermission("billiards:memberLevelConfig:list")
    @GetMapping("/list")
    public TableDataInfo<BlsMemberLevelConfigVo> list(BlsMemberLevelConfigBo bo, PageQuery pageQuery) {
        return blsMemberLevelConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出会员等级配置列表
     */
    @SaCheckPermission("billiards:memberLevelConfig:export")
    @Log(title = "会员等级配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsMemberLevelConfigBo bo, HttpServletResponse response) {
        List<BlsMemberLevelConfigVo> list = blsMemberLevelConfigService.queryList(bo);
        ExcelUtil.exportExcel(list, "会员等级配置", BlsMemberLevelConfigVo.class, response);
    }

    /**
     * 获取会员等级配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:memberLevelConfig:query")
    @GetMapping("/{id}")
    public R<BlsMemberLevelConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsMemberLevelConfigService.queryById(id));
    }

    /**
     * 新增会员等级配置
     */
    @SaCheckPermission("billiards:memberLevelConfig:add")
    @Log(title = "会员等级配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsMemberLevelConfigBo bo) {
        return toAjax(blsMemberLevelConfigService.insertByBo(bo));
    }

    /**
     * 修改会员等级配置
     */
    @SaCheckPermission("billiards:memberLevelConfig:edit")
    @Log(title = "会员等级配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsMemberLevelConfigBo bo) {
        return toAjax(blsMemberLevelConfigService.updateByBo(bo));
    }

    /**
     * 删除会员等级配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:memberLevelConfig:remove")
    @Log(title = "会员等级配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsMemberLevelConfigService.deleteWithValidByIds(List.of(ids), true));
    }
}
