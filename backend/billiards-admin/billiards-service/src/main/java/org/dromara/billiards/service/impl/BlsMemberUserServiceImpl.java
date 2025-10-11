package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.MemberChangeTypeEnum;
import org.dromara.billiards.common.constant.PointsSceneEnum;
import org.dromara.billiards.domain.entity.*;
import org.dromara.billiards.notify.event.MemberLevelChangedEvent;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.dromara.billiards.domain.bo.BlsMemberUserBo;
import org.dromara.billiards.domain.vo.BlsMemberUserVo;
import org.dromara.billiards.mapper.BlsMemberUserMapper;
import org.dromara.billiards.service.IBlsMemberUserService;
import org.dromara.billiards.service.IBlsMemberLevelConfigService;
import org.dromara.billiards.service.IBlsMemberPointsRecordService;
import org.dromara.billiards.service.IBlsMemberPointsValidityService;
import org.dromara.billiards.service.IBlsPointsRuleService;
import org.dromara.billiards.domain.bo.BlsMemberPointsRecordBo;
import org.dromara.billiards.domain.bo.BlsMemberPointsValidityBo;
import org.dromara.billiards.domain.bo.BlsMemberPointsConsumeDetailBo;
import org.dromara.billiards.service.IBlsMemberPointsConsumeDetailService;
import org.dromara.billiards.service.IBlsMemberChangeLogService;
import org.dromara.billiards.domain.bo.BlsMemberChangeLogBo;
import org.dromara.billiards.domain.bo.BlsPointsRuleBo;

import java.util.List;
import java.util.Collection;
import java.util.Date;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 会员用户Service业务层处理
 *
 * @author banyue
 * @date 2025-06-17
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsMemberUserServiceImpl extends ServiceImpl<BlsMemberUserMapper, BlsMemberUser> implements IBlsMemberUserService {

    private final IBlsMemberLevelConfigService memberLevelConfigService;
    private final IBlsMemberPointsRecordService memberPointsRecordService;
    private final IBlsMemberPointsValidityService memberPointsValidityService;
    private final IBlsPointsRuleService pointsRuleService;
    private final IBlsMemberPointsConsumeDetailService consumeDetailService;
    private final IBlsMemberChangeLogService memberChangeLogService;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 查询会员用户
     *
     * @param id 主键
     * @return 会员用户
     */
    @Override
    public BlsMemberUserVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员用户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员用户分页列表
     */
    @Override
    public TableDataInfo<BlsMemberUserVo> queryPageList(BlsMemberUserBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsMemberUser> lqw = buildQueryWrapper(bo);
        Page<BlsMemberUserVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员用户列表
     *
     * @param bo 查询条件
     * @return 会员用户列表
     */
    @Override
    public List<BlsMemberUserVo> queryList(BlsMemberUserBo bo) {
        LambdaQueryWrapper<BlsMemberUser> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsMemberUser> buildQueryWrapper(BlsMemberUserBo bo) {
        LambdaQueryWrapper<BlsMemberUser> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsMemberUser::getId);
        lqw.eq(bo.getUserId() != null, BlsMemberUser::getUserId, bo.getUserId());
        lqw.eq(bo.getLevelCode() != null, BlsMemberUser::getLevelCode, bo.getLevelCode());
        lqw.eq(bo.getTotalAmount() != null, BlsMemberUser::getTotalAmount, bo.getTotalAmount());
        lqw.eq(bo.getPoints() != null, BlsMemberUser::getPoints, bo.getPoints());
        lqw.eq(bo.getMonthlyUsedMinutes() != null, BlsMemberUser::getMonthlyUsedMinutes, bo.getMonthlyUsedMinutes());
        lqw.eq(bo.getLevelExpireTime() != null, BlsMemberUser::getLevelExpireTime, bo.getLevelExpireTime());
        lqw.eq(bo.getLastConsumeTime() != null, BlsMemberUser::getLastConsumeTime, bo.getLastConsumeTime());
        lqw.eq(bo.getStatus() != null, BlsMemberUser::getStatus, bo.getStatus());
        // 可选商户范围过滤
        MerchantQueryHelper.apply(lqw, BlsMemberUser::getMerchantId);
        return lqw;
    }

    /**
     * 新增会员用户
     *
     * @param bo 会员用户
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsMemberUserBo bo) {
        BlsMemberUser add = MapstructUtils.convert(bo, BlsMemberUser.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改会员用户
     *
     * @param bo 会员用户
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsMemberUserBo bo) {
        BlsMemberUser update = MapstructUtils.convert(bo, BlsMemberUser.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsMemberUser entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除会员用户信息
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
    @Transactional(rollbackFor = Exception.class)
    public void accrueOnPaidOrder(BlsOrder order) {
        // 幂等：如已对该订单入账过获取类-消费积分，则跳过
        BlsMemberPointsRecordBo existBo = new BlsMemberPointsRecordBo();
        existBo.setBusinessId(order.getId());
        if (memberPointsRecordService.queryList(existBo) != null && !memberPointsRecordService.queryList(existBo).isEmpty()) {
            return;
        }

        // 1) 获取或创建会员用户行（按租户上下文+merchantId+userId）
        BlsMemberUser member = baseMapper.selectOne(Wrappers.<BlsMemberUser>lambdaQuery()
            .eq(StringUtils.isNotEmpty(order.getMerchantId()), BlsMemberUser::getMerchantId, order.getMerchantId())
            .eq(BlsMemberUser::getUserId, order.getUserId())
            .last("LIMIT 1"));
        if (member == null) {
            member = new BlsMemberUser();
            member.setUserId(order.getUserId());
            member.setMerchantId(order.getMerchantId());
            member.setTenantId(order.getTenantId());
            member.setTotalAmount(BigDecimal.ZERO);
            member.setPoints(0L);
            member.setStatus(0L);
            baseMapper.insert(member);
        }

        // 2) 累计消费金额 + 最近消费时间
        BigDecimal newTotal = (member.getTotalAmount() == null ? BigDecimal.ZERO : member.getTotalAmount()).add(order.getActualAmount());
        member.setTotalAmount(newTotal);
        member.setLastConsumeTime(LocalDateTime.now());

        // 3) 等级检查（按累计金额），并记录变更日志
        String beforeLevel = member.getLevelCode();
        String bestLevel = computeBestLevelByTotalAmount(newTotal);
        if (bestLevel != null && (beforeLevel == null || !bestLevel.equals(beforeLevel))) {
            member.setLevelCode(bestLevel);
            BlsMemberChangeLog changeBo = new BlsMemberChangeLog();
            changeBo.setMerchantId(order.getMerchantId());
            changeBo.setTenantId(order.getTenantId());
            changeBo.setUserId(order.getUserId());
            changeBo.setChangeType(MemberChangeTypeEnum.UPGRADE.getCode());
            changeBo.setBeforeLevel(beforeLevel);
            changeBo.setAfterLevel(bestLevel);
            changeBo.setBeforeExpire(member.getLevelExpireTime());
            changeBo.setAfterExpire(member.getLevelExpireTime());
            changeBo.setOrderId(order.getId());
            changeBo.setRemark("累计消费升级");
            memberChangeLogService.save(changeBo);
            // 异步事件：会员升级
            eventPublisher.publishEvent(new MemberLevelChangedEvent(this,
                member.getUserId(), member.getMerchantId(), beforeLevel, bestLevel, order.getId()));
        }

        // 4) 计算积分（基础积分 × 等级倍率）
        BigDecimal multiplier = BigDecimal.ONE;
        List<BlsMemberLevelConfig> allLevels = memberLevelConfigService.queryList();
        if (member.getLevelCode() != null && allLevels != null) {
            for (BlsMemberLevelConfig cfg : allLevels) {
                if (member.getLevelCode().equals(cfg.getLevelCode())) {
                    if (cfg.getPointsMultiplier() != null) {
                        multiplier = cfg.getPointsMultiplier();
                    }
                    break;
                }
            }
        }

        // 基础积分规则（类型=获取，场景=消费(1)）；没有规则则退化为按金额取整
        long basePoints = order.getActualAmount().setScale(0, BigDecimal.ROUND_FLOOR).longValue();
        BlsPointsRuleBo ruleBo = new BlsPointsRuleBo();
        ruleBo.setType(1L);
        ruleBo.setScene(1L);
        List<BlsPointsRule> rules = MapstructUtils.convert(pointsRuleService.queryList(ruleBo), BlsPointsRule.class);
        if (rules != null && !rules.isEmpty()) {
            BlsPointsRule r = rules.get(0);
            if (r.getValueType() != null && r.getPointsValue() != null) {
                if (r.getValueType() == 1L) {
                    basePoints = r.getPointsValue();
                } else if (r.getValueType() == 2L) {
                    basePoints = order.getActualAmount().multiply(BigDecimal.valueOf(r.getPointsValue()))
                        .setScale(0, BigDecimal.ROUND_FLOOR).longValue();
                }
            }
        }
        long finalPoints = multiplier == null ? basePoints : multiplier.multiply(BigDecimal.valueOf(basePoints)).setScale(0, BigDecimal.ROUND_FLOOR).longValue();
        if (finalPoints < 0) {
            finalPoints = 0;
        }

        // 5) 记积分记录（增加）
        if (finalPoints > 0) {
            BlsMemberPointsRecord recordBo = new BlsMemberPointsRecord();
            recordBo.setMerchantId(order.getMerchantId());
            recordBo.setTenantId(order.getTenantId());
            recordBo.setUserId(order.getUserId());
            recordBo.setPoints(finalPoints);
            recordBo.setType(1L); // 获取
            recordBo.setScene(1L); // 消费
            recordBo.setBusinessId(order.getId());
            if (rules != null && !rules.isEmpty()) {
                recordBo.setRuleId(rules.get(0).getId());
            }
            recordBo.setDescription("消费入账");
            // expireTime 同有效期
            Integer validityDays = (rules != null && !rules.isEmpty() && rules.get(0).getValidityDays() != null)
                ? rules.get(0).getValidityDays() : 365;
            LocalDateTime expire = LocalDateTime.now().plusDays(validityDays);
            recordBo.setExpireTime(expire);
            memberPointsRecordService.save(recordBo);

            // 6) 生成有效期批次
            BlsMemberPointsValidity validityBo = new BlsMemberPointsValidity();
            validityBo.setUserId(order.getUserId());
            validityBo.setMerchantId(order.getMerchantId());
            validityBo.setTenantId(order.getTenantId());
            validityBo.setPoints(finalPoints);
            validityBo.setRemainingPoints(finalPoints);
            validityBo.setExpireTime(recordBo.getExpireTime());
            memberPointsValidityService.save(validityBo);

            // 7) 回写会员积分汇总
            long current = member.getPoints() == null ? 0L : member.getPoints();
            member.setPoints(current + finalPoints);
        }

        // 最后保存会员用户
        baseMapper.updateById(member);
    }

    @Override
    public void checkAndUpdateLevel(Long userId, String merchantId) {
        if (userId == null) return;
        BlsMemberUser member = baseMapper.selectOne(Wrappers.<BlsMemberUser>lambdaQuery()
            .eq(StringUtils.isNotEmpty(merchantId), BlsMemberUser::getMerchantId, merchantId)
            .eq(BlsMemberUser::getUserId, userId)
            .last("LIMIT 1"));
        if (member == null) return;
        BigDecimal total = member.getTotalAmount() == null ? BigDecimal.ZERO : member.getTotalAmount();
        String beforeLevel = member.getLevelCode();
        String bestLevel = computeBestLevelByTotalAmount(total);
        if (bestLevel != null && (beforeLevel == null || !bestLevel.equals(beforeLevel))) {
            member.setLevelCode(bestLevel);
            BlsMemberChangeLog changeBo = new BlsMemberChangeLog();
            changeBo.setMerchantId(member.getMerchantId());
            changeBo.setUserId(userId);
            changeBo.setChangeType(MemberChangeTypeEnum.UPGRADE.getCode());
            changeBo.setBeforeLevel(beforeLevel);
            changeBo.setAfterLevel(bestLevel);
            changeBo.setBeforeExpire(member.getLevelExpireTime());
            changeBo.setAfterExpire(member.getLevelExpireTime());
            changeBo.setRemark("累计消费升级");
            memberChangeLogService.save(changeBo);
            // 异步事件：会员升级
            eventPublisher.publishEvent(new MemberLevelChangedEvent(this,
                member.getUserId(), member.getMerchantId(), beforeLevel, bestLevel, null));
        }
        baseMapper.updateById(member);
    }

    @Override
    public BlsMemberUser queryUserMember(Long userId, String merchantId) {
        return baseMapper.selectOne(Wrappers.<BlsMemberUser>lambdaQuery()
            .eq(StringUtils.isNotEmpty(merchantId), BlsMemberUser::getMerchantId, merchantId)
            .eq(BlsMemberUser::getUserId, userId)
            .last("LIMIT 1"));
    }

    private String computeBestLevelByTotalAmount(BigDecimal totalAmount){
        List<BlsMemberLevelConfig> allLevels = memberLevelConfigService.queryList();
        if (allLevels == null || allLevels.isEmpty()) return null;
        allLevels.sort((a, b) -> a.getRequiredAmount().compareTo(b.getRequiredAmount()));
        String bestLevel = null;
        for (BlsMemberLevelConfig cfg : allLevels) {
            if (cfg.getRequiredAmount() != null && totalAmount.compareTo(cfg.getRequiredAmount()) >= 0) {
                try {
                    bestLevel = cfg.getLevelCode();
                } catch (Exception ignored) {}
            }
        }
        return bestLevel;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal deductPointsFifo(Long userId, String merchantId, String businessId, long pointsToDeduct, Long scene, String ruleId, Long maxPointsAllowed) {
        if (userId == null || pointsToDeduct <= 0) {
            return BigDecimal.ZERO;
        }
        // 1) 计算可用积分（未过期批次）
        LocalDateTime now = LocalDateTime.now();
        List<BlsMemberPointsValidity> buckets = memberPointsValidityService.queryList(new BlsMemberPointsValidityBo()).stream()
            .map(v -> MapstructUtils.convert(v, BlsMemberPointsValidity.class))
            .filter(v -> v.getUserId() != null && v.getUserId().equals(userId))
            .filter(v -> v.getMerchantId() != null && v.getMerchantId().equals(merchantId))
            .filter(v -> v.getExpireTime() == null || v.getExpireTime().isAfter(now))
            .sorted((a, b) -> a.getExpireTime().compareTo(b.getExpireTime()))
            .toList();

        long available = buckets.stream().mapToLong(v -> v.getRemainingPoints() == null ? 0L : v.getRemainingPoints()).sum();
        if (maxPointsAllowed != null && pointsToDeduct > maxPointsAllowed) {
            throw new IllegalArgumentException("points exceed single limit");
        }
        if (pointsToDeduct > available) {
//            throw new IllegalArgumentException("insufficient points");
            // 最大抵扣数量超过可用积分时，按照100积分整数抵扣，不足100积分不抵扣
            pointsToDeduct = available / 100 * 100;
        }
        if(pointsToDeduct == 0){
            return BigDecimal.ZERO;
        }
        long remaining = pointsToDeduct;
        // 2) 写一条消费流水主记录
        BlsMemberPointsRecord recordBo = new BlsMemberPointsRecord();
        recordBo.setUserId(userId);
        recordBo.setPoints(remaining);
        recordBo.setType(2L); // 消耗
        recordBo.setScene(PointsSceneEnum.GET_CONSUME.getCode());
        recordBo.setRuleId(ruleId);
        recordBo.setBusinessId(businessId);
        recordBo.setDescription("积分抵扣");
        memberPointsRecordService.save(recordBo);

        // 3) FIFO扣减并写消费明细
        for (BlsMemberPointsValidity bucket : buckets) {
            if (remaining <= 0) break;
            long canUse = bucket.getRemainingPoints() == null ? 0L : bucket.getRemainingPoints();
            if (canUse <= 0) continue;
            long used = Math.min(canUse, remaining);

            // 写明细
            BlsMemberPointsConsumeDetail detailBo = new BlsMemberPointsConsumeDetail();
            detailBo.setUserId(userId);
            detailBo.setMerchantId(merchantId);
            detailBo.setRecordId(recordBo.getId());
            detailBo.setValidityId(bucket.getId());
            detailBo.setPoints(used);
            consumeDetailService.save(detailBo);

            // 更新批次剩余
            BlsMemberPointsValidity updateBo = new BlsMemberPointsValidity();
            updateBo.setId(bucket.getId());
            updateBo.setRemainingPoints(canUse - used);
            memberPointsValidityService.saveOrUpdate(updateBo);

            remaining -= used;
        }

        // 4) 回写会员积分汇总
        BlsMemberUser member = queryUserMember(userId, merchantId);
        if (member != null) {
            long current = member.getPoints() == null ? 0L : member.getPoints();
            member.setPoints(current - pointsToDeduct);
            baseMapper.updateById(member);
        }

        // 返回本次抵扣的金额
        return BigDecimal.valueOf(pointsToDeduct / 100);
    }
}
