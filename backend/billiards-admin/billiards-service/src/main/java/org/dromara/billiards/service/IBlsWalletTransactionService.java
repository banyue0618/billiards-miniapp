package org.dromara.billiards.service;

import org.dromara.billiards.common.constant.TransTypeEnum;
import org.dromara.billiards.domain.bo.BlsWalletTransactionBo;
import org.dromara.billiards.domain.vo.BlsWalletTransactionVo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 用户钱包流水Service接口
 *
 * @author banyue
 * @date 2025-06-08
 */
public interface IBlsWalletTransactionService {

    /**
     * 查询用户钱包流水
     *
     * @param id 主键
     * @return 用户钱包流水
     */
    BlsWalletTransactionVo queryById(String id);

    /**
     * 分页查询用户钱包流水列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户钱包流水分页列表
     */
    TableDataInfo<BlsWalletTransactionVo> queryPageList(BlsWalletTransactionBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户钱包流水列表
     *
     * @param bo 查询条件
     * @return 用户钱包流水列表
     */
    List<BlsWalletTransactionVo> queryList(BlsWalletTransactionBo bo);

    /**
     * 新增用户钱包流水
     *
     * @param bo 用户钱包流水
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsWalletTransactionBo bo);

    /**
     * 修改用户钱包流水
     *
     * @param bo 用户钱包流水
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsWalletTransactionBo bo);

    /**
     * 校验并批量删除用户钱包流水信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    Boolean addWalletTransaction(Long userId, BigDecimal amount, String payRecordId, String transactionId, String remark, TransTypeEnum transType);
}
