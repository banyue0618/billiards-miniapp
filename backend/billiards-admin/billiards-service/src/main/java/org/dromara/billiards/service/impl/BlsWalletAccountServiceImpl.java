package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.bo.BlsWalletAccountBo;
import org.dromara.billiards.domain.entity.BlsWalletAccount;
import org.dromara.billiards.domain.vo.BlsWalletAccountVo;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.mapper.BlsWalletAccountMapper;
import org.dromara.billiards.service.IBlsWalletAccountService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Collection;

/**
 * 用户钱包账户Service业务层处理
 *
 * @author banyue
 * @date 2025-06-08
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsWalletAccountServiceImpl implements IBlsWalletAccountService {

    private final BlsWalletAccountMapper baseMapper;

    /**
     * 查询用户钱包账户
     *
     * @param id 主键
     * @return 用户钱包账户
     */
    @Override
    public BlsWalletAccountVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询用户钱包账户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户钱包账户分页列表
     */
    @Override
    public TableDataInfo<BlsWalletAccountVo> queryPageList(BlsWalletAccountBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsWalletAccount> lqw = buildQueryWrapper(bo);
        Page<BlsWalletAccountVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的用户钱包账户列表
     *
     * @param bo 查询条件
     * @return 用户钱包账户列表
     */
    @Override
    public List<BlsWalletAccountVo> queryList(BlsWalletAccountBo bo) {
        LambdaQueryWrapper<BlsWalletAccount> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsWalletAccount> buildQueryWrapper(BlsWalletAccountBo bo) {
        LambdaQueryWrapper<BlsWalletAccount> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsWalletAccount::getId);
        lqw.eq(bo.getUserId() != null, BlsWalletAccount::getUserId, bo.getUserId());
        lqw.eq(bo.getBalance() != null, BlsWalletAccount::getBalance, bo.getBalance());
        lqw.eq(bo.getTotalRecharge() != null, BlsWalletAccount::getTotalRecharge, bo.getTotalRecharge());
        lqw.eq(bo.getTotalRefund() != null, BlsWalletAccount::getTotalRefund, bo.getTotalRefund());
        return lqw;
    }

    /**
     * 新增用户钱包账户
     *
     * @param bo 用户钱包账户
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsWalletAccountBo bo) {
        BlsWalletAccount add = MapstructUtils.convert(bo, BlsWalletAccount.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改用户钱包账户
     *
     * @param bo 用户钱包账户
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsWalletAccountBo bo) {
        BlsWalletAccount update = MapstructUtils.convert(bo, BlsWalletAccount.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsWalletAccount entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户钱包账户信息
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
    public Boolean updateWalletBalance(Long userId, BigDecimal amount, String remark) {

        // 查询用户钱包账户
        BlsWalletAccount account = baseMapper.selectById(userId);
        if (account == null) {
            return false; // 用户钱包账户不存在
        }

        // 更新余额
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        // 更新总充值或退款金额
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            // 如果是充值
            account.setTotalRecharge(account.getTotalRecharge().add(amount));
        } else {
            // 如果是退款
            account.setTotalRefund(account.getTotalRefund().add(amount.abs()));
        }
        account.setRemark(remark);

        // 更新数据库
        return baseMapper.updateById(account) > 0;
    }
}
