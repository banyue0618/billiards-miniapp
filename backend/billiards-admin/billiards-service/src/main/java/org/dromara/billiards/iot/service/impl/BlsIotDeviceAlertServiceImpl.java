package org.dromara.billiards.iot.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.iot.domain.BlsIotDeviceAlert;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceAlertBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceAlertVo;
import org.dromara.billiards.iot.mapper.BlsIotDeviceAlertMapper;
import org.dromara.billiards.iot.service.IBlsIotDeviceAlertService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备告警（记录设备异常信息）Service业务层处理
 *
 * @author banyue
 * @date 2025-10-16
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsIotDeviceAlertServiceImpl extends ServiceImpl<BlsIotDeviceAlertMapper, BlsIotDeviceAlert> implements IBlsIotDeviceAlertService {

    /**
     * 查询设备告警（记录设备异常信息）
     *
     * @param id 主键
     * @return 设备告警（记录设备异常信息）
     */
    @Override
    public BlsIotDeviceAlertVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备告警（记录设备异常信息）列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备告警（记录设备异常信息）分页列表
     */
    @Override
    public TableDataInfo<BlsIotDeviceAlertVo> queryPageList(BlsIotDeviceAlertBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsIotDeviceAlert> lqw = buildQueryWrapper(bo);
        Page<BlsIotDeviceAlertVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备告警（记录设备异常信息）列表
     *
     * @param bo 查询条件
     * @return 设备告警（记录设备异常信息）列表
     */
    @Override
    public List<BlsIotDeviceAlertVo> queryList(BlsIotDeviceAlertBo bo) {
        LambdaQueryWrapper<BlsIotDeviceAlert> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsIotDeviceAlert> buildQueryWrapper(BlsIotDeviceAlertBo bo) {
        LambdaQueryWrapper<BlsIotDeviceAlert> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsIotDeviceAlert::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getAlertType()), BlsIotDeviceAlert::getAlertType, bo.getAlertType());
        lqw.eq(StringUtils.isNotBlank(bo.getAlertLevel()), BlsIotDeviceAlert::getAlertLevel, bo.getAlertLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceCode()), BlsIotDeviceAlert::getDeviceCode, bo.getDeviceCode());
        lqw.like(StringUtils.isNotBlank(bo.getDeviceName()), BlsIotDeviceAlert::getDeviceName, bo.getDeviceName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BlsIotDeviceAlert::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增设备告警（记录设备异常信息）
     *
     * @param bo 设备告警（记录设备异常信息）
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsIotDeviceAlertBo bo) {
        BlsIotDeviceAlert add = MapstructUtils.convert(bo, BlsIotDeviceAlert.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备告警（记录设备异常信息）
     *
     * @param bo 设备告警（记录设备异常信息）
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsIotDeviceAlertBo bo) {
        BlsIotDeviceAlert update = MapstructUtils.convert(bo, BlsIotDeviceAlert.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsIotDeviceAlert entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除设备告警（记录设备异常信息）信息
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
}
