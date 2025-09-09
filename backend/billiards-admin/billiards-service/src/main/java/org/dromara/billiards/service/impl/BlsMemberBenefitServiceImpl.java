package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsMemberLevelConfig;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.domain.bo.BlsMemberBenefitBo;
import org.dromara.billiards.domain.vo.BlsMemberBenefitVo;
import org.dromara.billiards.domain.entity.BlsMemberBenefit;
import org.dromara.billiards.mapper.BlsMemberBenefitMapper;
import org.dromara.billiards.service.IBlsMemberBenefitService;

import java.util.List;
import java.util.Collection;

/**
 * 会员权益Service业务层处理
 *
 * @author banyue
 * @date 2025-06-17
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsMemberBenefitServiceImpl implements IBlsMemberBenefitService {

    private final BlsMemberBenefitMapper baseMapper;

    /**
     * 查询会员权益
     *
     * @param id 主键
     * @return 会员权益
     */
    @Override
    public BlsMemberBenefitVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员权益列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员权益分页列表
     */
    @Override
    public TableDataInfo<BlsMemberBenefitVo> queryPageList(BlsMemberBenefitBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsMemberBenefit> lqw = buildQueryWrapper(bo);
        Page<BlsMemberBenefitVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员权益列表
     *
     * @param bo 查询条件
     * @return 会员权益列表
     */
    @Override
    public List<BlsMemberBenefitVo> queryList(BlsMemberBenefitBo bo) {
        LambdaQueryWrapper<BlsMemberBenefit> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsMemberBenefit> buildQueryWrapper(BlsMemberBenefitBo bo) {
        LambdaQueryWrapper<BlsMemberBenefit> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsMemberBenefit::getId);
        lqw.like(StringUtils.isNotBlank(bo.getName()), BlsMemberBenefit::getName, bo.getName());
        lqw.eq(bo.getType() != null, BlsMemberBenefit::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getApplicableLevels()), BlsMemberBenefit::getApplicableLevels, bo.getApplicableLevels());
        lqw.eq(StringUtils.isNotBlank(bo.getBenefitValue()), BlsMemberBenefit::getBenefitValue, bo.getBenefitValue());
        lqw.eq(StringUtils.isNotBlank(bo.getBenefitRules()), BlsMemberBenefit::getBenefitRules, bo.getBenefitRules());
        lqw.eq(bo.getEffectiveTime() != null, BlsMemberBenefit::getEffectiveTime, bo.getEffectiveTime());
        lqw.eq(bo.getExpireTime() != null, BlsMemberBenefit::getExpireTime, bo.getExpireTime());
        lqw.eq(StringUtils.isNotBlank(bo.getIcon()), BlsMemberBenefit::getIcon, bo.getIcon());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), BlsMemberBenefit::getDescription, bo.getDescription());
        lqw.eq(StringUtils.isNotBlank(bo.getInstructions()), BlsMemberBenefit::getInstructions, bo.getInstructions());
        lqw.eq(bo.getStatus() != null, BlsMemberBenefit::getStatus, bo.getStatus());
        lqw.eq(bo.getSortOrder() != null, BlsMemberBenefit::getSortOrder, bo.getSortOrder());
        lqw.eq(bo.getIsLimited() != null, BlsMemberBenefit::getIsLimited, bo.getIsLimited());
        lqw.eq(bo.getIsHoliday() != null, BlsMemberBenefit::getIsHoliday, bo.getIsHoliday());
        lqw.eq(StringUtils.isNotBlank(bo.getTags()), BlsMemberBenefit::getTags, bo.getTags());
        return lqw;
    }

    /**
     * 新增会员权益
     *
     * @param bo 会员权益
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsMemberBenefitBo bo) {
        BlsMemberBenefit add = MapstructUtils.convert(bo, BlsMemberBenefit.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改会员权益
     *
     * @param bo 会员权益
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsMemberBenefitBo bo) {
        BlsMemberBenefit update = MapstructUtils.convert(bo, BlsMemberBenefit.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsMemberBenefit entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除会员权益信息
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
