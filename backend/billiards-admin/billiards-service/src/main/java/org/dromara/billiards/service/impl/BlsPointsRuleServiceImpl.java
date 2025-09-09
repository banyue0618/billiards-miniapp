package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsTableUsage;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.domain.bo.BlsPointsRuleBo;
import org.dromara.billiards.domain.vo.BlsPointsRuleVo;
import org.dromara.billiards.domain.entity.BlsPointsRule;
import org.dromara.billiards.mapper.BlsPointsRuleMapper;
import org.dromara.billiards.service.IBlsPointsRuleService;

import java.util.List;
import java.util.Collection;

/**
 * 积分规则Service业务层处理
 *
 * @author banyue
 * @date 2025-06-17
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsPointsRuleServiceImpl implements IBlsPointsRuleService {

    private final BlsPointsRuleMapper baseMapper;

    /**
     * 查询积分规则
     *
     * @param id 主键
     * @return 积分规则
     */
    @Override
    public BlsPointsRuleVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询积分规则列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 积分规则分页列表
     */
    @Override
    public TableDataInfo<BlsPointsRuleVo> queryPageList(BlsPointsRuleBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsPointsRule> lqw = buildQueryWrapper(bo);
        Page<BlsPointsRuleVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的积分规则列表
     *
     * @param bo 查询条件
     * @return 积分规则列表
     */
    @Override
    public List<BlsPointsRuleVo> queryList(BlsPointsRuleBo bo) {
        LambdaQueryWrapper<BlsPointsRule> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsPointsRule> buildQueryWrapper(BlsPointsRuleBo bo) {
        LambdaQueryWrapper<BlsPointsRule> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsPointsRule::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), BlsPointsRule::getName, bo.getName());
        lqw.eq(bo.getType() != null, BlsPointsRule::getType, bo.getType());
        lqw.eq(bo.getScene() != null, BlsPointsRule::getScene, bo.getScene());
        lqw.eq(bo.getValueType() != null, BlsPointsRule::getValueType, bo.getValueType());
        lqw.eq(bo.getPointsValue() != null, BlsPointsRule::getPointsValue, bo.getPointsValue());
        lqw.eq(bo.getMaxPoints() != null, BlsPointsRule::getMaxPoints, bo.getMaxPoints());
        lqw.eq(StringUtils.isNotBlank(bo.getRuleConfig()), BlsPointsRule::getRuleConfig, bo.getRuleConfig());
        lqw.eq(StringUtils.isNotBlank(bo.getLevelBonus()), BlsPointsRule::getLevelBonus, bo.getLevelBonus());
        lqw.eq(StringUtils.isNotBlank(bo.getTimeBonus()), BlsPointsRule::getTimeBonus, bo.getTimeBonus());
        lqw.eq(bo.getEffectiveTime() != null, BlsPointsRule::getEffectiveTime, bo.getEffectiveTime());
        lqw.eq(bo.getExpireTime() != null, BlsPointsRule::getExpireTime, bo.getExpireTime());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), BlsPointsRule::getDescription, bo.getDescription());
        lqw.eq(bo.getStatus() != null, BlsPointsRule::getStatus, bo.getStatus());
        lqw.eq(bo.getEnableActivityBonus() != null, BlsPointsRule::getEnableActivityBonus, bo.getEnableActivityBonus());
        lqw.eq(bo.getValidityDays() != null, BlsPointsRule::getValidityDays, bo.getValidityDays());
        return lqw;
    }

    /**
     * 新增积分规则
     *
     * @param bo 积分规则
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsPointsRuleBo bo) {
        BlsPointsRule add = MapstructUtils.convert(bo, BlsPointsRule.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改积分规则
     *
     * @param bo 积分规则
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsPointsRuleBo bo) {
        BlsPointsRule update = MapstructUtils.convert(bo, BlsPointsRule.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsPointsRule entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除积分规则信息
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
}
