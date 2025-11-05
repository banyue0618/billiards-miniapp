package org.dromara.billiards.controller.miniapp;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.domain.bo.BlsReservationBo;
import org.dromara.billiards.domain.vo.BlsReservationVo;
import org.dromara.billiards.service.IBlsReservationService;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序预约控制器
 *
 * @author banyue
 * @date 2025-11-03
 */
@Slf4j
@RestController("miniappReservationController")
@RequestMapping("/api/miniapp/reservation")
@RequiredArgsConstructor
@Tag(name = "预约接口", description = "小程序预约相关接口")
public class ReservationController {

    private final IBlsReservationService reservationService;

    /**
     * 预约桌台
     */
    @PostMapping("/reserve")
    @Operation(summary = "预约桌台", description = "用户预约指定桌台的时间段")
    @Log(title = "用户预约", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    public R<BlsReservationVo> reserve(@Validated(AddGroup.class) @RequestBody BlsReservationBo bo) {
        BlsReservationVo result = reservationService.reserve(bo);
        return ApiResult.success(result);
    }

    /**
     * 获取用户的预约列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取预约列表", description = "获取当前用户的预约记录列表")
    public R<IPage<BlsReservationVo>> getReservationList(BlsReservationBo bo, PageQuery pageQuery) {
        return ApiResult.success(reservationService.queryPage(bo, pageQuery));
    }

    /**
     * 获取用户进行中的预约列表
     */
    @GetMapping("/current")
    @Operation(summary = "获取进行中的预约记录", description = "获取用户进行中的预约记录")
    public R<BlsReservationVo> current() {
        return ApiResult.success(reservationService.current());
    }

    /**
     * 取消预约
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "取消预约", description = "取消指定的预约记录")
    @Log(title = "取消预约", businessType = BusinessType.UPDATE)
    public R<Void> cancelReservation(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ApiResult.success();
    }
}

