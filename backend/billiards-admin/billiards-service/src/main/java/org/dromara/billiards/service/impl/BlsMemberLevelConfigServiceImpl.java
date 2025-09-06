package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsMemberPointsRecord;
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
import org.dromara.billiards.domain.bo.BlsMemberLevelConfigBo;
import org.dromara.billiards.domain.vo.BlsMemberLevelConfigVo;
import org.dromara.billiards.domain.entity.BlsMemberLevelConfig;
import org.dromara.billiards.mapper.BlsMemberLevelConfigMapper;
import org.dromara.billiards.service.IBlsMemberLevelConfigService;

import java.util.List;
import java.util.Collection;

/**
 * 会员等级配置Service业务层处理
 *
 * @author banyue
 * @date 2025-06-17
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsMemberLevelConfigServiceImpl implements IBlsMemberLevelConfigService {

    private final BlsMemberLevelConfigMapper baseMapper;

    /**
     * 查询会员等级配置
     *
     * @param id 主键
     * @return 会员等级配置
     */
    @Override
    public BlsMemberLevelConfigVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询会员等级配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员等级配置分页列表
     */
    @Override
    public TableDataInfo<BlsMemberLevelConfigVo> queryPageList(BlsMemberLevelConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsMemberLevelConfig> lqw = buildQueryWrapper(bo);
        Page<BlsMemberLevelConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的会员等级配置列表
     *
     * @param bo 查询条件
     * @return 会员等级配置列表
     */
    @Override
    public List<BlsMemberLevelConfigVo> queryList(BlsMemberLevelConfigBo bo) {
        LambdaQueryWrapper<BlsMemberLevelConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsMemberLevelConfig> buildQueryWrapper(BlsMemberLevelConfigBo bo) {
        LambdaQueryWrapper<BlsMemberLevelConfig> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsMemberLevelConfig::getId);
        lqw.eq(bo.getLevelCode() != null, BlsMemberLevelConfig::getLevelCode, bo.getLevelCode());
        lqw.like(StringUtils.isNotBlank(bo.getLevelName()), BlsMemberLevelConfig::getLevelName, bo.getLevelName());
        lqw.eq(bo.getRequiredAmount() != null, BlsMemberLevelConfig::getRequiredAmount, bo.getRequiredAmount());
        lqw.eq(bo.getDiscount() != null, BlsMemberLevelConfig::getDiscount, bo.getDiscount());
        lqw.eq(bo.getMonthlyFreeMinutes() != null, BlsMemberLevelConfig::getMonthlyFreeMinutes, bo.getMonthlyFreeMinutes());
        lqw.eq(bo.getPointsMultiplier() != null, BlsMemberLevelConfig::getPointsMultiplier, bo.getPointsMultiplier());
        lqw.eq(bo.getBirthdayDiscount() != null, BlsMemberLevelConfig::getBirthdayDiscount, bo.getBirthdayDiscount());
        lqw.eq(bo.getFriendPrivilegeCount() != null, BlsMemberLevelConfig::getFriendPrivilegeCount, bo.getFriendPrivilegeCount());
        lqw.eq(bo.getVipService() != null, BlsMemberLevelConfig::getVipService, bo.getVipService());
        lqw.eq(bo.getReservationPrivilege() != null, BlsMemberLevelConfig::getReservationPrivilege, bo.getReservationPrivilege());
        lqw.eq(StringUtils.isNotBlank(bo.getLevelIcon()), BlsMemberLevelConfig::getLevelIcon, bo.getLevelIcon());
        lqw.eq(StringUtils.isNotBlank(bo.getLevelBackground()), BlsMemberLevelConfig::getLevelBackground, bo.getLevelBackground());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), BlsMemberLevelConfig::getDescription, bo.getDescription());
        lqw.eq(bo.getStatus() != null, BlsMemberLevelConfig::getStatus, bo.getStatus());
        // 可选商户范围过滤
        MerchantQueryHelper.apply(lqw, BlsMemberLevelConfig::getMerchantId);
        return lqw;
    }

    /**
     * 新增会员等级配置
     *
     * @param bo 会员等级配置
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsMemberLevelConfigBo bo) {
        BlsMemberLevelConfig add = MapstructUtils.convert(bo, BlsMemberLevelConfig.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改会员等级配置
     *
     * @param bo 会员等级配置
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsMemberLevelConfigBo bo) {
        BlsMemberLevelConfig update = MapstructUtils.convert(bo, BlsMemberLevelConfig.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsMemberLevelConfig entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除会员等级配置信息
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
