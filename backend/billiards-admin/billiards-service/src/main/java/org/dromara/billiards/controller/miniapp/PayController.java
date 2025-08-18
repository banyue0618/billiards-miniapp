package org.dromara.billiards.controller.miniapp;

import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.OrderChannelEnum;
import org.dromara.billiards.domain.bo.PaymentRequest;
import org.dromara.billiards.service.IBlsRefundRecordService;
import org.dromara.billiards.service.IBlsPayRecordService;
import org.dromara.common.core.domain.R;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 支付相关接口
 *
 * @author zhangsip
 * @date 2024-07-02
 */
@Slf4j
@RestController
@RequestMapping("/api/miniapp/payment")
@Tag(name = "支付接口", description = "小程序支付相关接口")
public class PayController {
    @Resource
    private IBlsPayRecordService payRecordService;

    @Resource
    private IBlsRefundRecordService refundRecordService;

    /**
     * 创建支付订单
     */
    @PostMapping("/pay")
    @Operation(summary = "创建支付订单", description = "创建微信支付预支付订单")
    public R<String> createPayment(@RequestBody @Valid PaymentRequest request) {
        String payParams = payRecordService.createPayment(request, OrderChannelEnum.MINI_PROGRAM.getChannelName());
        return R.ok("操作成功", payParams);
    }

    /**
     * 支付结果通知（在支付完成之后，微信会回调此接口）
     */
    @PostMapping("/payNotify")
    @Operation(summary = "支付结果通知", description = "接收微信支付结果通知")
    public ResponseEntity<String> payNotify(@RequestBody String notifyData, HttpServletRequest request) {
        boolean handled = payRecordService.handlePayNotify(notifyData, request);
        if (handled) {
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body("fail");
    }

    /**
     * 退款结果通知（在发起退款之后，微信会回调此接口）
     */
    @PostMapping("/refundNotify")
    @Operation(summary = "退款结果通知", description = "接收微信退款结果通知")
    public ResponseEntity<String> refundNotify(@RequestBody String notifyData, HttpServletRequest request) {
        boolean handled = refundRecordService.handleRefundNotify(notifyData, request);
        if (handled) {
            return ResponseEntity.ok("success");
        }
        return ResponseEntity.status(HttpServletResponse.SC_BAD_REQUEST).body("fail");
    }

    /**
     * 主动查询支付结果
     */
    @GetMapping("/query/{payNo}")
    @Operation(summary = "查询支付结果", description = "主动查询支付结果")
    public R<Boolean> queryPayResult(@Parameter(description = "支付流水号") @PathVariable String payNo) throws WxPayException {
        String payStatus = payRecordService.queryPayStatus(null, payNo);
        return R.ok("查询支付结果成功", WxPayConstants.ResultCode.SUCCESS.equals(payStatus));
    }
}
