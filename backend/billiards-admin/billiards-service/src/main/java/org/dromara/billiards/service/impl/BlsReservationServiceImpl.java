package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.entity.BlsReservation;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.domain.bo.BlsReservationBo;
import org.dromara.billiards.domain.vo.BlsReservationVo;
import org.dromara.billiards.mapper.BlsReservationMapper;
import org.dromara.billiards.service.IBlsReservationService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 用户预约记录Service业务层处理
 *
 * @author banyue
 * @date 2025-11-03
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsReservationServiceImpl  extends ServiceImpl<BlsReservationMapper, BlsReservation> implements IBlsReservationService{

    /**
     * 查询用户预约记录
     *
     * @param id 主键
     * @return 用户预约记录
     */
    @Override
    public BlsReservationVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询用户预约记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 用户预约记录分页列表
     */
    @Override
    public TableDataInfo<BlsReservationVo> queryPageList(BlsReservationBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsReservation> lqw = buildQueryWrapper(bo);
        Page<BlsReservationVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的用户预约记录列表
     *
     * @param bo 查询条件
     * @return 用户预约记录列表
     */
    @Override
    public List<BlsReservationVo> queryList(BlsReservationBo bo) {
        LambdaQueryWrapper<BlsReservation> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsReservation> buildQueryWrapper(BlsReservationBo bo) {
        LambdaQueryWrapper<BlsReservation> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsReservation::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getReservationNo()), BlsReservation::getReservationNo, bo.getReservationNo());
        lqw.eq(bo.getStartTime() != null, BlsReservation::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, BlsReservation::getEndTime, bo.getEndTime());
        lqw.eq(bo.getStatus() != null, BlsReservation::getStatus, bo.getStatus());
        lqw.eq(bo.getPayStatus() != null, BlsReservation::getPayStatus, bo.getPayStatus());
        return lqw;
    }

    /**
     * 新增用户预约记录
     *
     * @param bo 用户预约记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsReservationBo bo) {
        BlsReservation add = MapstructUtils.convert(bo, BlsReservation.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改用户预约记录
     *
     * @param bo 用户预约记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsReservationBo bo) {
        BlsReservation update = MapstructUtils.convert(bo, BlsReservation.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsReservation entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除用户预约记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public BlsReservationVo reserve(BlsReservationBo bo) {
        // 用户预约

        // 判断当前桌台预约时间段不重叠

        // 新增预约记录

        // 发送微信小程序消息通知


        return null;
    }
}
