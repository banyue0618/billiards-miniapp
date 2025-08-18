package org.dromara.common.pay.service;

import cn.hutool.json.JSONUtil;
import com.github.binarywang.wxpay.bean.notify.SignatureHeader;
import com.github.binarywang.wxpay.bean.notify.WxPayNotifyV3Result;
import com.github.binarywang.wxpay.bean.notify.WxPayRefundNotifyV3Result;
import com.github.binarywang.wxpay.bean.request.WxPayOrderQueryV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayRefundV3Request;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderV3Request;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayRefundV3Result;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderV3Result;
import com.github.binarywang.wxpay.bean.result.enums.TradeTypeEnum;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Description 微信支付
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/6/2
 */
@Slf4j
@Service
public class PayService {

    @Resource
    private WxPayService wxService;

    @Value("${billiards.payment.notify-url:/api/miniapp/payment/payNotify}")
    private String payNotifyUrl;

    @Value("${billiards.payment.notify-url:/api/miniapp/payment/refundNotify}")
    private String refundNotifyUrl;

    /**
     * 查询订单
     * @param transactionId
     * @param outTradeNo
     * @return
     * @throws WxPayException
     */
    public WxPayOrderQueryV3Result queryOrder(String transactionId, String outTradeNo) throws WxPayException {
        WxPayOrderQueryV3Request wxPayOrderQueryV3Request = new WxPayOrderQueryV3Request();
        wxPayOrderQueryV3Request.setTransactionId(transactionId);
        wxPayOrderQueryV3Request.setOutTradeNo(outTradeNo);
        if (transactionId == null && outTradeNo == null) {
            throw new WxPayException("Either transactionId or outTradeNo must be provided");
        }
        // 调用微信支付服务查询订单
        return this.wxService.queryOrderV3(wxPayOrderQueryV3Request);
    }

    public void refund(String transactionId, String outTradeNo, BigDecimal refundAmount, String outRefundNo) throws WxPayException {
        WxPayRefundV3Request request = new WxPayRefundV3Request();
        request.setTransactionId(transactionId);
        request.setOutTradeNo(outTradeNo);
        request.setOutRefundNo(outRefundNo);
        WxPayRefundV3Request.Amount amount = new WxPayRefundV3Request.Amount();
        amount.setCurrency("CNY");
        amount.setRefund(refundAmount.intValue());
        request.setAmount(amount);

        request.setNotifyUrl(refundNotifyUrl);

        // 发起退款请求
        wxService.refundV3(request);
    }

    public String jsApiPay(String openId, BigDecimal amount, String outTradeNo) throws WxPayException {

        WxPayUnifiedOrderV3Request request = new WxPayUnifiedOrderV3Request();
        WxPayUnifiedOrderV3Request.Payer payer = new WxPayUnifiedOrderV3Request.Payer();
        payer.setOpenid(openId);
        request.setPayer(payer);

        WxPayUnifiedOrderV3Request.Amount payAmount = new WxPayUnifiedOrderV3Request.Amount();
        payAmount.setTotal(amount.intValue());
        payAmount.setCurrency("CNY");
        request.setAmount(payAmount);

        request.setOutTradeNo(outTradeNo);

        request.setNotifyUrl(payNotifyUrl);

        WxPayUnifiedOrderV3Result.JsapiResult jsapiResult = wxService.createOrderV3(TradeTypeEnum.JSAPI, request);

        // 返回prepayId，packageValue为组合字符串,格式如 "prepay_id=wx201410272009395522657a690389285100"
        if (jsapiResult == null || jsapiResult.getPackageValue() == null) {
            throw new WxPayException("创建JSAPI支付订单失败，返回结果为空");
        }
        log.info("创建JSAPI支付订单成功，返回结果: {}", JSONUtil.toJsonStr(jsapiResult));
        return jsapiResult.getPackageValue().split("=")[1]; // 返回预支付订单的prepay_id
    }

    /**
     * 支付回调通知处理
     * @param notifyData
     * @param request
     * @return
     * @throws WxPayException
     */
    public WxPayNotifyV3Result parseOrderNotifyResult(String notifyData, HttpServletRequest request) throws WxPayException {
        SignatureHeader header = getRequestHeader(request);
        return wxService.parseOrderNotifyV3Result(notifyData, header);
    }


    /**
     * 退款回调通知处理
     * @param notifyData
     * @param request
     * @return
     * @throws WxPayException
     */
    public WxPayRefundNotifyV3Result parseRefundNotifyResult(String notifyData, HttpServletRequest request) throws WxPayException {
        SignatureHeader header = getRequestHeader(request);
        return wxService.parseRefundNotifyV3Result(notifyData, header);
    }

    /**
     * 组装请求头重的前面信息
     *
     * @param request
     * @return
     */
    private SignatureHeader getRequestHeader(HttpServletRequest request) {
        // 获取通知签名
        String signature = request.getHeader("Wechatpay-Signature");
        String nonce = request.getHeader("Wechatpay-Nonce");
        String serial = request.getHeader("Wechatpay-Serial");
        String timestamp = request.getHeader("Wechatpay-Timestamp");

        SignatureHeader signatureHeader = new SignatureHeader();
        signatureHeader.setSignature(signature);
        signatureHeader.setNonce(nonce);
        signatureHeader.setSerial(serial);
        signatureHeader.setTimeStamp(timestamp);
        return signatureHeader;
    }

}
