package org.dromara.billiards.controller.miniapp;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 小程序通用接口控制器
 * 提供一些通用的配置和状态码信息
 *
 * @author banyue
 * @date 2025-11-03
 */
@Slf4j
@RestController("miniappCommonController")
@RequestMapping("/api/miniapp/common")
@RequiredArgsConstructor
@Tag(name = "通用接口", description = "小程序通用配置接口")
public class CommonController {

    /**
     * 获取所有错误码映射
     * 用于前端初始化时加载，避免硬编码错误码
     *
     * @return 错误码与错误消息的映射
     */
    @GetMapping("/error-codes")
    @Operation(summary = "获取错误码映射", description = "获取所有业务错误码与错误消息的映射关系")
    public R<Map<Integer, String>> getErrorCodes() {
        Map<Integer, String> errorCodeMap = new HashMap<>();
        
        // 遍历所有错误码枚举值
        for (ResultCode code : ResultCode.values()) {
            errorCodeMap.put(code.getCode(), code.getMessage());
        }
        
        return ApiResult.success(errorCodeMap);
    }
}

