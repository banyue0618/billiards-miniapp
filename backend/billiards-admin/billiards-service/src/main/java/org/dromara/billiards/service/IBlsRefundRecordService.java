package org.dromara.billiards.service;

import com.github.binarywang.wxpay.bean.result.WxPayRefundQueryV3Result;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.billiards.domain.bo.BlsRefundRecordBo;
import org.dromara.billiards.domain.entity.BlsRefundRecord;
import org.dromara.billiards.domain.entity.BlsPayRecord;
import org.dromara.billiards.domain.vo.BlsRefundRecordVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 退款记录Service接口
 *
 * @author banyue
 * @date 2025-06-08
 */
public interface IBlsRefundRecordService {

    /**
     * 查询退款记录
     *
     * @param id 主键
     * @return 退款记录
     */
    BlsRefundRecordVo queryById(String id);

    /**
     * 分页查询退款记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 退款记录分页列表
     */
    TableDataInfo<BlsRefundRecordVo> queryPageList(BlsRefundRecordBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的退款记录列表
     *
     * @param bo 查询条件
     * @return 退款记录列表
     */
    List<BlsRefundRecordVo> queryList(BlsRefundRecordBo bo);

    /**
     * 新增退款记录
     *
     * @param bo 退款记录
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsRefundRecordBo bo);

    /**
     * 修改退款记录
     *
     * @param bo 退款记录
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsRefundRecordBo bo);

    /**
     * 校验并批量删除退款记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);


    BlsRefundRecord queryRecordByOrderId(String orderId);

    /**
     * 发起退款
     * @param orderId
     * @param lastBlsPayRecord
     * @param deductBalance
     */
    void refund(String orderId, BlsPayRecord lastBlsPayRecord, BigDecimal deductBalance);

    /**
     * 微信退款回调接口
     * @return
     */
    boolean handleRefundNotify(String notifyData, HttpServletRequest request);


    /**
     * 主动查询退款结果
     * @param outRefundNo
     * @return
     * @throws Exception
     */
    WxPayRefundQueryV3Result queryRefundResult(String outRefundNo) throws Exception;

    /**
     * 处理退款结果
     * @param refundStatus
     * @param refundRecord
     * @return
     */
    boolean handleRefundResult(String refundStatus, BlsRefundRecord refundRecord, boolean ifThrowException);


    /**
     * 查询退款失败的记录
     *
     * @return 退款记录列表
     */
    List<BlsRefundRecordVo> queryRefundFailiureList();
}
