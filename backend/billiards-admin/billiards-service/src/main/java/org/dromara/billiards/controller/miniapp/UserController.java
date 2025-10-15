package org.dromara.billiards.controller.miniapp;

import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.domain.bo.RefundRequest;
import org.dromara.billiards.domain.bo.UserUpdateDto;
import org.dromara.billiards.domain.vo.RechargeRecordVO;
import org.dromara.billiards.domain.vo.UserVO;
import org.dromara.billiards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 微信小程序用户控制器
 */
@Slf4j
@RestController("miniappUserController")
@RequestMapping("/api/miniapp/user")
@RequiredArgsConstructor
@Tag(name = "用户接口", description = "小程序用户相关接口")
public class UserController {

    private final UserService userService;

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取用户信息", description = "获取当前登录用户的详细信息")
    public R<UserVO> getUserInfo() {
        return ApiResult.success(userService.getUserInfo());
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/update")
    @Operation(summary = "更新用户信息", description = "更新当前登录用户的个人资料")
    public R<Boolean> updateUserInfo(@Validated @RequestBody UserUpdateDto dto) {
        userService.updateCurrentUserInfo(dto);
        return ApiResult.success();
    }

    /**
     * 绑定手机号
     */
    @PostMapping("/bind-phone")
    @Operation(summary = "绑定手机号", description = "为当前登录用户绑定手机号")
    public R<Boolean> bindPhone(@RequestParam String phone) {
        boolean success = userService.bindUserPhone(phone);
        return ApiResult.success(success);
    }

    /**
     * 余额检查
     */
    @GetMapping("/scanTableEnableCheck")
    @Operation(summary = "余额检查", description = "检查当前用户是否满足开台条件")
    public R<Boolean> scanTableEnableCheck() {
        return ApiResult.success(userService.scanTableEnableCheck());
    }

    /**
     * 获取充值记录列表
     */
    @GetMapping("/recharge/list")
    @Operation(summary = "获取充值记录", description = "获取当前用户的充值记录列表")
    public R<List<RechargeRecordVO>> getRechargeList() {
        return ApiResult.success(userService.getRechargeList());
    }

    /**
     * 申请退款
     */
    @PostMapping("/refund/apply")
    @Operation(summary = "申请退款", description = "对指定的充值记录申请退款")
    public R<Boolean> applyRefund(@Validated @RequestBody RefundRequest request) {
        userService.applyRefund(request.getPayRecordId());
        return ApiResult.success();
    }
}
