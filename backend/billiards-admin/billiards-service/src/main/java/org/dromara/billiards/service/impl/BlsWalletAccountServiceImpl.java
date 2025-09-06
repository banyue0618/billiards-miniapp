package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.TransTypeEnum;
import org.dromara.billiards.domain.bo.BlsWalletAccountBo;
import org.dromara.billiards.domain.entity.BlsWalletAccount;
import org.dromara.billiards.domain.entity.BlsPayRecord;
import org.dromara.billiards.domain.entity.BlsWalletTransaction;
import org.dromara.billiards.domain.vo.BlsWalletAccountVo;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.billiards.service.IBlsWalletTransactionService;
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
@Slf4j
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsWalletAccountServiceImpl implements IBlsWalletAccountService {

    private final BlsWalletAccountMapper baseMapper;

    private final IBlsWalletTransactionService walletTransactionService;

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
        // 可选商户范围过滤
        MerchantQueryHelper.apply(lqw, BlsWalletAccount::getMerchantId);
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
    public BlsWalletAccount updateWalletBalance(Long userId, BigDecimal amount) {

        // 查询用户钱包账户
        BlsWalletAccount account = getWalletAccountByUserId(userId);

        BigDecimal currentBalance = account.getBalance();

        // 更新余额
        BigDecimal newBalance = currentBalance.add(amount);
        account.setBalance(newBalance);

        // 更新总充值或退款金额
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            // 如果是充值
            account.setTotalRecharge(account.getTotalRecharge().add(amount));
        } else {
            // 如果是退款
            account.setTotalRefund(account.getTotalRefund().add(amount.abs()));
            // 如果冻结金额大于 0 ，释放本次退款的冻结金额额度
            if(account.getFreezeAmount().compareTo(BigDecimal.ZERO) > 0){
                account.setFreezeAmount(account.getFreezeAmount().add(amount));
            }
        }

        // 更新数据库
        if(baseMapper.updateById(account) == 0){
            throw new RuntimeException("更新钱包余额失败");
        }
        log.info("更新用户余额: userId={}, 原余额={}, 变动金额={}, 新余额={}, 结果={}",
            userId, currentBalance, amount, newBalance, "成功");
        return account;
    }

    @Override
    public BlsWalletAccount initWalletAccount(Long userId) {
        // 初始化一条钱包记录
        BlsWalletAccount account = new BlsWalletAccount();
        account.setUserId(userId);
        account.setBalance(BigDecimal.ZERO);
        account.setTotalRecharge(BigDecimal.ZERO);
        account.setTotalRefund(BigDecimal.ZERO);
        account.setRemark("初始化钱包账户");
        // 插入数据库
        baseMapper.insert(account);

        return account;
    }

    @Override
    public BlsWalletAccount getWalletAccountByUserId(Long userId) {
        if (userId == null) {
            return null; // 用户ID为空，返回null
        }
        LambdaQueryWrapper<BlsWalletAccount> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlsWalletAccount::getUserId, userId);
        BlsWalletAccount walletAccount = baseMapper.selectOne(lqw);
        if (walletAccount == null) {
            // 如果没有钱包账户，初始化一个
            walletAccount = initWalletAccount(userId);
        }
        return walletAccount;
    }

    @Override
    public BigDecimal deductBalance(Long userId, BigDecimal amount) {
        BlsWalletAccount blsWalletAccount = updateWalletBalance(userId, amount.negate());

        BigDecimal freezeAmount = blsWalletAccount.getBalance();

        // 剩余金额大于0，需要退款，并且冻结当前金额
        if(freezeAmount.compareTo(BigDecimal.ZERO) > 0){
            // 余额全部冻结，待退款
            blsWalletAccount.setFreezeAmount(freezeAmount);
            blsWalletAccount.setBalance(BigDecimal.ZERO);

            // 更新钱包账户状态
            if(baseMapper.updateById(blsWalletAccount) == 0 ){
                throw new RuntimeException("扣减余额失败，更新钱包账户状态失败");
            }
        }

        // 返回扣减后的余额
        return freezeAmount;
    }

    @Override
    public BlsWalletAccount updateWalletBalanceAndWalletTransaction(BlsPayRecord blsPayRecord) {
        // 新增钱包流水记录 BlsWalletTransaction
        walletTransactionService.addWalletTransaction(blsPayRecord.getUserId(), blsPayRecord.getAmount(), blsPayRecord.getId(), blsPayRecord.getTransactionId(), blsPayRecord.getRemark(), TransTypeEnum.RECHARGE);

        // 更新钱包 BlsWalletAccount
        return updateWalletBalance(blsPayRecord.getUserId(), blsPayRecord.getAmount().negate());
    }
}
