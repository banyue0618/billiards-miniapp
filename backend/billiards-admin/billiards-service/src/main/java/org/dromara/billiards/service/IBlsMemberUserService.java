package org.dromara.billiards.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.domain.entity.BlsMemberUser;
import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.vo.BlsMemberUserVo;
import org.dromara.billiards.domain.bo.BlsMemberUserBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;
import java.math.BigDecimal;

/**
 * 会员用户Service接口
 *
 * @author banyue
 * @date 2025-06-17
 */
public interface IBlsMemberUserService extends IService<BlsMemberUser> {

    /**
     * 查询会员用户
     *
     * @param id 主键
     * @return 会员用户
     */
    BlsMemberUserVo queryById(String id);

    /**
     * 分页查询会员用户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员用户分页列表
     */
    TableDataInfo<BlsMemberUserVo> queryPageList(BlsMemberUserBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员用户列表
     *
     * @param bo 查询条件
     * @return 会员用户列表
     */
    List<BlsMemberUserVo> queryList(BlsMemberUserBo bo);

    /**
     * 新增会员用户
     *
     * @param bo 会员用户
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsMemberUserBo bo);

    /**
     * 修改会员用户
     *
     * @param bo 会员用户
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsMemberUserBo bo);

    /**
     * 校验并批量删除会员用户信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 订单支付完成后，累计消费、等级校验并发放积分。
     *
     * @param order      用户ID
     */
    void accrueOnPaidOrder(BlsOrder order);

    /**
     * 按FIFO规则扣减积分（最早过期优先）。
     *
     * 业务约束：
     * 1) 扣减数量需 ≤ 可用积分（未过期批次的 remaining_points 之和）
     * 2) 可选地校验单次上限（传入 maxPointsAllowed 不为 null 时生效）
     *
     * @param userId           用户ID
     * @param merchantId       商户ID
     * @param businessId       业务ID（如订单ID）
     * @param pointsToDeduct   需要扣减的积分（正数）
     * @param scene            场景（与积分规则一致，消费抵扣例如 1）
     * @param ruleId           使用的规则ID（可选）
     * @param maxPointsAllowed 单次最多可扣积分（可为 null）
     * @return 本地抵扣的金额
     */
    BigDecimal deductPointsFifo(Long userId, String merchantId, String businessId, long pointsToDeduct, Long scene, String ruleId, Long maxPointsAllowed);

    /**
     * 根据累计消费金额检查并更新用户等级（支持升级/降级）。
     * @param userId     用户ID
     * @param merchantId 商户ID
     */
    void checkAndUpdateLevel(Long userId, String merchantId);


    BlsMemberUser queryUserMember(Long userId, String merchantId);
}
