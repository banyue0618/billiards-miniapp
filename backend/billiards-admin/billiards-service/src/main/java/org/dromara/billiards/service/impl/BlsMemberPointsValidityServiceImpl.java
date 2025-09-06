package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsMemberUser;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.domain.bo.BlsMemberPointsValidityBo;
import org.dromara.billiards.domain.vo.BlsMemberPointsValidityVo;
import org.dromara.billiards.domain.entity.BlsMemberPointsValidity;
import org.dromara.billiards.mapper.BlsMemberPointsValidityMapper;
import org.dromara.billiards.service.IBlsMemberPointsValidityService;

import java.util.List;
import java.util.Collection;

/**
 * 积分有效期Service业务层处理
 *
 * @author banyue
 * @date 2025-06-17
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsMemberPointsValidityServiceImpl implements IBlsMemberPointsValidityService {

    private final BlsMemberPointsValidityMapper baseMapper;

    /**
     * 查询积分有效期
     *
     * @param id 主键
     * @return 积分有效期
     */
    @Override
    public BlsMemberPointsValidityVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询积分有效期列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 积分有效期分页列表
     */
    @Override
    public TableDataInfo<BlsMemberPointsValidityVo> queryPageList(BlsMemberPointsValidityBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsMemberPointsValidity> lqw = buildQueryWrapper(bo);
        Page<BlsMemberPointsValidityVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的积分有效期列表
     *
     * @param bo 查询条件
     * @return 积分有效期列表
     */
    @Override
    public List<BlsMemberPointsValidityVo> queryList(BlsMemberPointsValidityBo bo) {
        LambdaQueryWrapper<BlsMemberPointsValidity> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsMemberPointsValidity> buildQueryWrapper(BlsMemberPointsValidityBo bo) {
        LambdaQueryWrapper<BlsMemberPointsValidity> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsMemberPointsValidity::getId);
        lqw.eq(bo.getUserId() != null, BlsMemberPointsValidity::getUserId, bo.getUserId());
        lqw.eq(bo.getPoints() != null, BlsMemberPointsValidity::getPoints, bo.getPoints());
        lqw.eq(bo.getRemainingPoints() != null, BlsMemberPointsValidity::getRemainingPoints, bo.getRemainingPoints());
        lqw.eq(bo.getExpireTime() != null, BlsMemberPointsValidity::getExpireTime, bo.getExpireTime());
        // 可选商户范围过滤
        MerchantQueryHelper.apply(lqw, BlsMemberPointsValidity::getMerchantId);
        return lqw;
    }

    /**
     * 新增积分有效期
     *
     * @param bo 积分有效期
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsMemberPointsValidityBo bo) {
        BlsMemberPointsValidity add = MapstructUtils.convert(bo, BlsMemberPointsValidity.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改积分有效期
     *
     * @param bo 积分有效期
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsMemberPointsValidityBo bo) {
        BlsMemberPointsValidity update = MapstructUtils.convert(bo, BlsMemberPointsValidity.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsMemberPointsValidity entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除积分有效期信息
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
