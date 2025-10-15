package org.dromara.billiards.controller.miniapp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.domain.bo.BlsMemberPointsRecordBo;
import org.dromara.billiards.domain.vo.BlsMemberPointsRecordVo;
import org.dromara.billiards.service.IBlsMemberPointsRecordService;
import org.dromara.common.core.domain.R;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.springframework.web.bind.annotation.*;

/**
 * 微信小程序积分控制器
 *
 * @author banyue
 */
@Slf4j
@RestController("miniappPointsController")
@RequestMapping("/api/miniapp/points")
@RequiredArgsConstructor
@Tag(name = "积分接口", description = "小程序积分相关接口")
public class PointsController {

    private final IBlsMemberPointsRecordService memberPointsRecordService;

    /**
     * 查询当前用户积分记录列表
     *
     * @param type      积分类型：1-获取 2-消耗
     * @param pageQuery 分页参数
     * @return 积分记录分页列表
     */
    @GetMapping("/records")
    @Operation(summary = "查询积分记录", description = "查询当前用户的积分记录列表，支持按类型筛选和分页")
    public R<TableDataInfo<BlsMemberPointsRecordVo>> getPointsRecords(
            @Parameter(description = "积分类型：1-获取 2-消耗，0-全部")
            @RequestParam(required = false) Long type,
            PageQuery pageQuery) {

        // 获取当前登录用户ID
        Long userId = LoginHelper.getUserId();

        // 构建查询条件
        BlsMemberPointsRecordBo bo = new BlsMemberPointsRecordBo();
        bo.setUserId(userId);
        bo.setType(type);

        // 查询积分记录
        TableDataInfo<BlsMemberPointsRecordVo> result = memberPointsRecordService.queryPageList(bo, pageQuery);

        return ApiResult.success(result);
    }

    /**
     * 查询积分记录详情
     *
     * @param id 积分记录ID
     * @return 积分记录详情
     */
    @GetMapping("/records/{id}")
    @Operation(summary = "查询积分记录详情", description = "根据ID查询积分记录的详细信息")
    public R<BlsMemberPointsRecordVo> getPointsRecordDetail(
            @Parameter(description = "积分记录ID")
            @PathVariable String id) {

        BlsMemberPointsRecordVo record = memberPointsRecordService.queryById(id);

        // 验证记录是否属于当前用户
        Long userId = LoginHelper.getUserId();
        if (record == null || !userId.equals(record.getUserId())) {
            return ApiResult.error("记录不存在或无权访问");
        }

        return ApiResult.success(record);
    }
}

