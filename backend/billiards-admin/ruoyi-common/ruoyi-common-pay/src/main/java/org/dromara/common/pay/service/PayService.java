package org.dromara.common.pay.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.pay.config.HttpKit;
import org.dromara.common.pay.config.PayKit;
import org.dromara.common.pay.config.WxPayKit;
import org.dromara.common.pay.config.WxPayV3Bean;
import org.dromara.common.pay.enums.BasePayApiEnum;
import org.dromara.common.pay.enums.OtherApiEnum;
import org.dromara.common.pay.enums.RequestMethodEnum;
import org.dromara.common.pay.enums.WxDomainEnum;
import org.dromara.common.pay.model.v3.*;
import org.dromara.common.pay.response.PayHttpResponse;
import org.dromara.common.pay.util.AesUtil;
import org.dromara.common.pay.util.DateTimeZoneUtil;
import org.dromara.common.pay.util.WxPayApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 微信支付相关
 * @Author zhangsip
 * @Version V1.0.0
 * @Since 1.0
 * @Date 2025/6/2
 */
@Slf4j
@Service
public class PayService {

    @Resource
    private WxPayV3Bean wxPayV3Bean;

    String serialNo;
    String platSerialNo;

    public String refund(String transactionId, String outTradeNo, BigDecimal amount, String outRefundNo){

        try {
            log.info("商户退款单号: {}", outRefundNo);

            List<RefundGoodsDetail> list = new ArrayList<>();
            RefundGoodsDetail refundGoodsDetail = new RefundGoodsDetail()
                .setMerchant_goods_id("123")
                .setGoods_name("自助天球")
                .setUnit_price(amount)
                .setRefund_amount(amount)
                .setRefund_quantity(1);
            list.add(refundGoodsDetail);

            RefundModel refundModel = new RefundModel()
                .setOut_refund_no(outRefundNo)
                .setReason("余额结算退回")
                .setNotify_url(wxPayV3Bean.getDomain().concat(wxPayV3Bean.getRefundNotifyUri()))
                .setAmount(new RefundAmount().setRefund(amount).setTotal(amount).setCurrency("CNY"))
                .setGoods_detail(list);

            if (StrUtil.isNotEmpty(transactionId)) {
                refundModel.setTransaction_id(transactionId);
            }
            if (StrUtil.isNotEmpty(outTradeNo)) {
                refundModel.setOut_trade_no(outTradeNo);
            }
            log.info("退款参数 {}", JSONUtil.toJsonStr(refundModel));
            PayHttpResponse response = WxPayApi.v3(
                RequestMethodEnum.POST,
                WxDomainEnum.CHINA.toString(),
                BasePayApiEnum.REFUND.toString(),
                wxPayV3Bean.getMchId(),
                getSerialNumber(),
                null,
                wxPayV3Bean.getKeyPath(),
                JSONUtil.toJsonStr(refundModel)
            );
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            log.info("退款响应 {}", response);

            if (verifySignature) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("退款异常: {}", e.getMessage());
            return e.getMessage();
        }
        return null;

    }


    public String jsApiPay(String openId, BigDecimal amount, String outTradeNo) {
        try {
            String timeExpire = DateTimeZoneUtil.dateToTimeZone(System.currentTimeMillis() + 1000 * 60 * 3);
            UnifiedOrderModel unifiedOrderModel = new UnifiedOrderModel()
                .setAppid(wxPayV3Bean.getAppId())
                .setMchid(wxPayV3Bean.getMchId())
                .setDescription("IJPay 让支付触手可及")
                .setOut_trade_no(outTradeNo)
                .setTime_expire(timeExpire)
                .setNotify_url(wxPayV3Bean.getDomain().concat(wxPayV3Bean.getPayNotifyUri()))
                .setAmount(new Amount().setTotal(amount))
                .setPayer(new Payer().setOpenid(openId));

            log.info("统一下单参数 {}", JSONUtil.toJsonStr(unifiedOrderModel));
            PayHttpResponse response = WxPayApi.v3(
                RequestMethodEnum.POST,
                WxDomainEnum.CHINA.toString(),
                BasePayApiEnum.JS_API_PAY.toString(),
                wxPayV3Bean.getMchId(),
                getSerialNumber(),
                null,
                wxPayV3Bean.getKeyPath(),
                JSONUtil.toJsonStr(unifiedOrderModel)
            );
            log.info("统一下单响应 {}", response);
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            log.info("verifySignature: {}", verifySignature);
            if (response.getStatus() == 200 && verifySignature) {
                String body = response.getBody();
                JSONObject jsonObject = JSONUtil.parseObj(body);
                String prepayId = jsonObject.getStr("prepay_id");
                Map<String, String> map = WxPayKit.jsApiCreateSign(wxPayV3Bean.getAppId(), prepayId, wxPayV3Bean.getKeyPath());
                log.info("唤起支付参数:{}", map);
                return JSONUtil.toJsonStr(map);
            }
            return JSONUtil.toJsonStr(response);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 返回支付回调的明文信息，用于处理业务
     * @param request
     * @return
     */
    public String resolveNotifyData(HttpServletRequest request) {
        String plainText = null;
        try {
            String timestamp = request.getHeader("Wechatpay-Timestamp");
            String nonce = request.getHeader("Wechatpay-Nonce");
            String serialNo = request.getHeader("Wechatpay-Serial");
            String signature = request.getHeader("Wechatpay-Signature");

            log.info("timestamp:{} nonce:{} serialNo:{} signature:{}", timestamp, nonce, serialNo, signature);
            String result = HttpKit.readData(request);

            // 需要通过证书序列号查找对应的证书，verifyNotify 中有验证证书的序列号
            plainText = WxPayKit.verifyNotify(serialNo, result, signature, nonce, timestamp,
                wxPayV3Bean.getApiKey3(), wxPayV3Bean.getPlatformCertPath());
        } catch (Exception e) {
            log.error("支付通知异常: {}", e.getMessage());
        }
        return plainText;
    }


    public String v3Get() {
        // 获取平台证书列表
        try {
            PayHttpResponse response = WxPayApi.v3(
                RequestMethodEnum.GET,
                WxDomainEnum.CHINA.toString(),
                OtherApiEnum.GET_CERTIFICATES.toString(),
                wxPayV3Bean.getMchId(),
                getSerialNumber(),
                null,
                wxPayV3Bean.getKeyPath(),
                ""
            );

            String timestamp = response.getHeader("Wechatpay-Timestamp");
            String nonceStr = response.getHeader("Wechatpay-Nonce");
            String serialNumber = response.getHeader("Wechatpay-Serial");
            String signature = response.getHeader("Wechatpay-Signature");

            String body = response.getBody();
            int status = response.getStatus();

            log.info("serialNumber: {}", serialNumber);
            log.info("status: {}", status);
            log.info("body: {}", body);
            int isOk = 200;
            if (status == isOk) {
                JSONObject jsonObject = JSONUtil.parseObj(body);
                JSONArray dataArray = jsonObject.getJSONArray("data");
                // 默认认为只有一个平台证书
                JSONObject encryptObject = dataArray.getJSONObject(0);
                JSONObject encryptCertificate = encryptObject.getJSONObject("encrypt_certificate");
                String associatedData = encryptCertificate.getStr("associated_data");
                String cipherText = encryptCertificate.getStr("ciphertext");
                String nonce = encryptCertificate.getStr("nonce");
                String serialNo = encryptObject.getStr("serial_no");
                final String platSerialNo = savePlatformCert(associatedData, nonce, cipherText, wxPayV3Bean.getPlatformCertPath());
                log.info("平台证书序列号: {} serialNo: {}", platSerialNo, serialNo);
            }
            // 根据证书序列号查询对应的证书来验证签名结果
            boolean verifySignature = WxPayKit.verifySignature(response, wxPayV3Bean.getPlatformCertPath());
            System.out.println("verifySignature:" + verifySignature);
            return body;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getSerialNumber() {
        if (StrUtil.isEmpty(serialNo)) {
            // 获取证书序列号
            X509Certificate certificate = PayKit.getCertificate(wxPayV3Bean.getCertPath());
            if (null != certificate) {
                serialNo = certificate.getSerialNumber().toString(16).toUpperCase();
                // 提前两天检查证书是否有效
                boolean isValid = PayKit.checkCertificateIsValid(certificate, wxPayV3Bean.getMchId(), -2);
                log.info("证书是否可用 {} 证书有效期为 {}", isValid, DateUtil.format(certificate.getNotAfter(), DatePattern.NORM_DATETIME_PATTERN));
            }
//            System.out.println("输出证书信息:\n" + certificate.toString());
//            // 输出关键信息，截取部分并进行标记
//            System.out.println("证书序列号:" + certificate.getSerialNumber().toString(16));
//            System.out.println("版本号:" + certificate.getVersion());
//            System.out.println("签发者：" + certificate.getIssuerDN());
//            System.out.println("有效起始日期：" + certificate.getNotBefore());
//            System.out.println("有效终止日期：" + certificate.getNotAfter());
//            System.out.println("主体名：" + certificate.getSubjectDN());
//            System.out.println("签名算法：" + certificate.getSigAlgName());
//            System.out.println("签名：" + certificate.getSignature().toString());
        }
        System.out.println("serialNo:" + serialNo);
        return serialNo;
    }

    private String getPlatSerialNumber() {
        if (StrUtil.isEmpty(platSerialNo)) {
            // 获取平台证书序列号
            X509Certificate certificate = PayKit.getCertificate(FileUtil.getInputStream(wxPayV3Bean.getPlatformCertPath()));
            platSerialNo = certificate.getSerialNumber().toString(16).toUpperCase();
        }
        System.out.println("platSerialNo:" + platSerialNo);
        return platSerialNo;
    }

    private String savePlatformCert(String associatedData, String nonce, String cipherText, String certPath) {
        try {
            AesUtil aesUtil = new AesUtil(wxPayV3Bean.getApiKey3().getBytes(StandardCharsets.UTF_8));
            // 平台证书密文解密
            // encrypt_certificate 中的  associated_data nonce  ciphertext
            String publicKey = aesUtil.decryptToString(
                associatedData.getBytes(StandardCharsets.UTF_8),
                nonce.getBytes(StandardCharsets.UTF_8),
                cipherText
            );
            // 保存证书
            FileWriter writer = new FileWriter(certPath);
            writer.write(publicKey);
            // 获取平台证书序列号
            X509Certificate certificate = PayKit.getCertificate(new ByteArrayInputStream(publicKey.getBytes()));
            return certificate.getSerialNumber().toString(16).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    /**
     * 返回成功响应
     */
    public void responseSuccess(HttpServletResponse response) throws IOException {
        Map<String, String> map = new HashMap<>(2);
        map.put("returnCode", "SUCCESS");
        map.put("message", "成功");
        response.setStatus(200);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSONUtil.toJsonStr(map));
    }

    /**
     * 返回错误响应
     */
    public void responseError(HttpServletResponse response) throws IOException {
        Map<String, String> map = new HashMap<>(2);
        map.put("returnCode", "FAIL");
        map.put("message", "失败");
        response.setStatus(500);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(JSONUtil.toJsonStr(map));
    }
}
