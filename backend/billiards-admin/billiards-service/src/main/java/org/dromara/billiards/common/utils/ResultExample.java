package org.dromara.billiards.common.utils;

import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.common.result.ApiResult;
import org.dromara.billiards.common.result.ResultCode;
import org.dromara.common.core.domain.R;

/**
 * 这是一个示例类，展示如何在控制器中使用ApiResult替代Result
 * 该类仅用于参考，不参与实际业务逻辑
 */
public class ResultExample {

    /**
     * 示例1：返回成功结果（无数据）
     */
    public R<Void> example1() {
        // 原代码：return Result.success();
        return ApiResult.success();
    }

    /**
     * 示例2：返回成功结果（有数据）
     */
    public R<String> example2() {
        String data = "示例数据";
        // 原代码：return Result.success(data);
        return ApiResult.success(data);
    }

    /**
     * 示例3：返回失败结果（自定义消息）
     */
    public R<Void> example3() {
        // 原代码：return Result.error("操作失败，请重试");
        return ApiResult.error("操作失败，请重试");
    }

    /**
     * 示例4：返回失败结果（使用预定义的错误码）
     */
    public R<Void> example4() {
        // 原代码：return Result.error(ResultCode.UNAUTHORIZED);
        return ApiResult.error(ResultCode.UNAUTHORIZED);
    }

    /**
     * 示例5：处理业务异常
     */
    public void example5() {
        if (true) { // 示例条件，实际应根据业务逻辑判断
            // 原代码：throw new BusinessException(ResultCode.PARAM_ERROR);
            throw BilliardsException.of(ResultCode.PARAM_ERROR);
        }
    }

    /**
     * 示例6：处理业务异常（自定义消息）
     */
    public void example6() {
        String orderId = "1234567890";
        if (true) { // 示例条件，实际应根据业务逻辑判断
            // 原代码：throw new BusinessException(ResultCode.ORDER_EXPIRED, "订单ID: " + orderId);
            throw BilliardsException.of(ResultCode.ORDER_EXPIRED, "订单ID: " + orderId);
        }
    }

    /**
     * 示例7：使用业务常量
     */
    public void example7() {
        // 原代码：if (tableStatus == CommonConstant.STATUS_DISABLE) {
        if (1 == BilliardsConstants.TABLE_STATUS_USING) {
            // 处理桌台使用中的情况
        }
    }
}
