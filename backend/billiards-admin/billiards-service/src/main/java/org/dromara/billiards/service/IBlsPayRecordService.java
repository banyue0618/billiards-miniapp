package org.dromara.billiards.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.binarywang.wxpay.bean.result.WxPayOrderQueryV3Result;
import com.github.binarywang.wxpay.exception.WxPayException;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.billiards.common.constant.PaymentStatus;
import org.dromara.billiards.domain.bo.PaymentRequest;
import org.dromara.billiards.domain.entity.BlsPayRecord;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 充值支付记录 Service 接口
 *
 * @author zhangsip
 * @date 2024-07-02
 */
public interface IBlsPayRecordService extends IService<BlsPayRecord> {

    /**
     * 创建充值订单
     *
     * @param request  充值金额
     * @param channel 支付渠道
     * @return 支付参数（用于前端唤起支付）
     */
    String createPayment(PaymentRequest request, String channel);


    /**
     * 查询支付结果
     * @param transactionId
     * @param outTradeNo
     * @return
     * @throws WxPayException
     */
    WxPayOrderQueryV3Result queryPayStatus(String transactionId, String outTradeNo) throws WxPayException;


    /**
     * 处理支付结果通知
     *
     * @param xmlData
     * @param request
     * @return 处理结果
     */
    boolean handlePayNotify(String xmlData, HttpServletRequest request);

    /**
     * 获取用户最近的一条已支付记录
     * @param userId 用户ID
     * @return 最近的一条已支付记录
     */
    BlsPayRecord getLastPayRecord(Long userId);

    /**
     * 查询指定状态的记录
     * @param paymentStatus
     * @return
     */
    List<BlsPayRecord> queryListWithStatus(PaymentStatus paymentStatus);

    /**
     * 查询支付超时的订单
     * @param threshold
     * @return
     */
    List<BlsPayRecord> queryPayingTimeoutList(LocalDateTime threshold);
}
