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
import org.dromara.billiards.domain.vo.BlsMemberUserVo;
import org.dromara.billiards.domain.bo.BlsMemberUserBo;
import org.dromara.billiards.service.IBlsMemberUserService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 会员用户
 *
 * @author banyue
 * @date 2025-06-17
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/billiards/memberUser")
public class BlsMemberUserController extends BaseController {

    private final IBlsMemberUserService blsMemberUserService;

    /**
     * 查询会员用户列表
     */
    @SaCheckPermission("billiards:memberUser:list")
    @GetMapping("/list")
    public TableDataInfo<BlsMemberUserVo> list(BlsMemberUserBo bo, PageQuery pageQuery) {
        return blsMemberUserService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出会员用户列表
     */
    @SaCheckPermission("billiards:memberUser:export")
    @Log(title = "会员用户", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(BlsMemberUserBo bo, HttpServletResponse response) {
        List<BlsMemberUserVo> list = blsMemberUserService.queryList(bo);
        ExcelUtil.exportExcel(list, "会员用户", BlsMemberUserVo.class, response);
    }

    /**
     * 获取会员用户详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("billiards:memberUser:query")
    @GetMapping("/{id}")
    public R<BlsMemberUserVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable String id) {
        return R.ok(blsMemberUserService.queryById(id));
    }

    /**
     * 新增会员用户
     */
    @SaCheckPermission("billiards:memberUser:add")
    @Log(title = "会员用户", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody BlsMemberUserBo bo) {
        return toAjax(blsMemberUserService.insertByBo(bo));
    }

    /**
     * 修改会员用户
     */
    @SaCheckPermission("billiards:memberUser:edit")
    @Log(title = "会员用户", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody BlsMemberUserBo bo) {
        return toAjax(blsMemberUserService.updateByBo(bo));
    }

    /**
     * 删除会员用户
     *
     * @param ids 主键串
     */
    @SaCheckPermission("billiards:memberUser:remove")
    @Log(title = "会员用户", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable String[] ids) {
        return toAjax(blsMemberUserService.deleteWithValidByIds(List.of(ids), true));
    }
}
