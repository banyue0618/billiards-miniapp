package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.TransTypeEnum;
import org.dromara.billiards.domain.bo.BlsWalletTransactionBo;
import org.dromara.billiards.domain.entity.BlsWalletTransaction;
import org.dromara.billiards.domain.vo.BlsWalletTransactionVo;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.billiards.security.MerchantWriteGuard;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.mapper.BlsWalletTransactionMapper;
import org.dromara.billiards.service.IBlsWalletTransactionService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collection;

/**
 * 用户钱包流水Service业务层处理
 *
 * @author banyue
 * @date 2025-06-08
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsWalletTransactionServiceImpl implements IBlsWalletTransactionService {

    private final BlsWalletTransactionMapper baseMapper;

    /**
     * 查询用户钱包流水
     *
     * @param id 主键
     * @return 用户钱包流水
     */
    @Override
    public BlsWalletTransactionVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询用户钱包流水列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户钱包流水分页列表
     */
    @Override
    public TableDataInfo<BlsWalletTransactionVo> queryPageList(BlsWalletTransactionBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsWalletTransaction> lqw = buildQueryWrapper(bo);
        Page<BlsWalletTransactionVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的用户钱包流水列表
     *
     * @param bo 查询条件
     * @return 用户钱包流水列表
     */
    @Override
    public List<BlsWalletTransactionVo> queryList(BlsWalletTransactionBo bo) {
        LambdaQueryWrapper<BlsWalletTransaction> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsWalletTransaction> buildQueryWrapper(BlsWalletTransactionBo bo) {
        LambdaQueryWrapper<BlsWalletTransaction> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsWalletTransaction::getId);
        lqw.eq(bo.getUserId() != null, BlsWalletTransaction::getUserId, bo.getUserId());
        lqw.eq(StringUtils.isNotBlank(bo.getTransType()), BlsWalletTransaction::getTransType, bo.getTransType());
        lqw.eq(bo.getAmount() != null, BlsWalletTransaction::getAmount, bo.getAmount());
        lqw.eq(StringUtils.isNotBlank(bo.getRelatedId()), BlsWalletTransaction::getRelatedId, bo.getRelatedId());
        lqw.eq(StringUtils.isNotBlank(bo.getSourcePayId()), BlsWalletTransaction::getSourcePayId, bo.getSourcePayId());
        lqw.eq(StringUtils.isNotBlank(bo.getTransactionId()), BlsWalletTransaction::getTransactionId, bo.getTransactionId());
        // 可选商户范围过滤
        MerchantQueryHelper.apply(lqw, BlsWalletTransaction::getMerchantId);
        return lqw;
    }

    /**
     * 新增用户钱包流水
     *
     * @param bo 用户钱包流水
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsWalletTransactionBo bo) {
        BlsWalletTransaction add = MapstructUtils.convert(bo, BlsWalletTransaction.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改用户钱包流水
     *
     * @param bo 用户钱包流水
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsWalletTransactionBo bo) {
        BlsWalletTransaction update = MapstructUtils.convert(bo, BlsWalletTransaction.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsWalletTransaction entity){
    }

    /**
     * 校验并批量删除用户钱包流水信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public Boolean addWalletTransaction(Long userId, BigDecimal amount, String payRecordId, String transactionId, String remark, TransTypeEnum transType) {
        // 创建新的钱包流水记录
        BlsWalletTransaction transaction = new BlsWalletTransaction();
        transaction.setUserId(userId);
        transaction.setTransType(transType.getCode());
        transaction.setAmount(amount);
        transaction.setRelatedId(payRecordId);
        transaction.setTransactionId(transactionId);
        transaction.setRemark(remark);
        // 插入记录到数据库
        validEntityBeforeSave(transaction);
        return baseMapper.insert(transaction) > 0;
    }
}
