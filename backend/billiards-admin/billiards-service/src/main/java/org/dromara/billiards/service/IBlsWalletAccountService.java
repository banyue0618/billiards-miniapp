package org.dromara.billiards.service;

import org.dromara.billiards.domain.bo.BlsWalletAccountBo;
import org.dromara.billiards.domain.entity.BlsWalletAccount;
import org.dromara.billiards.domain.vo.BlsWalletAccountVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * 用户钱包账户Service接口
 *
 * @author banyue
 * @date 2025-06-08
 */
public interface IBlsWalletAccountService {

    /**
     * 查询用户钱包账户
     *
     * @param id 主键
     * @return 用户钱包账户
     */
    BlsWalletAccountVo queryById(String id);

    /**
     * 分页查询用户钱包账户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户钱包账户分页列表
     */
    TableDataInfo<BlsWalletAccountVo> queryPageList(BlsWalletAccountBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的用户钱包账户列表
     *
     * @param bo 查询条件
     * @return 用户钱包账户列表
     */
    List<BlsWalletAccountVo> queryList(BlsWalletAccountBo bo);

    /**
     * 新增用户钱包账户
     *
     * @param bo 用户钱包账户
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsWalletAccountBo bo);

    /**
     * 修改用户钱包账户
     *
     * @param bo 用户钱包账户
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsWalletAccountBo bo);

    /**
     * 校验并批量删除用户钱包账户信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 更新用户钱包余额
     * @param userId
     * @param amount
     * @return
     */
    BlsWalletAccount updateWalletBalance(Long userId, BigDecimal amount);

    /**
     * 初始化用户钱包账户
     * @param userId
     * @param initialBalance
     * @return
     */
    BlsWalletAccount initWalletAccount(Long userId, BigDecimal initialBalance);

    /**
     * 根据用户ID获取钱包账户
     * @param userId 用户ID
     * @return 钱包账户实体
     */
    BlsWalletAccount getWalletAccountByUserId(Long userId);

    /**
     * 扣减用户余额, 用户余额归0，返回冻结金额
     * @param userId
     * @param amount 扣减额
     * @return
     */
    BigDecimal deductBalance( Long userId, BigDecimal amount);
}
