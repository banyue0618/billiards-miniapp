package org.dromara.billiards.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dromara.billiards.domain.bo.PaymentRequest;
import org.dromara.billiards.domain.entity.PayRecord;

/**
 * 充值支付记录 Service 接口
 *
 * @author zhangsip
 * @date 2024-07-02
 */
public interface IPayRecordService extends IService<PayRecord> {

    /**
     * 创建充值订单
     *
     * @param request  充值金额
     * @param channel 支付渠道
     * @return 支付参数（用于前端唤起支付）
     */
    String createPayment(PaymentRequest request, String channel);

    /**
     * 处理支付结果通知
     *
     * @param request
     * @param response
     * @return 处理结果
     */
    boolean handlePayNotify(HttpServletRequest request, HttpServletResponse response);

    /**
     * 获取用户最近的一条已支付记录
     * @param userId 用户ID
     * @return 最近的一条已支付记录
     */
    PayRecord getLastPayRecord(Long userId);
}
