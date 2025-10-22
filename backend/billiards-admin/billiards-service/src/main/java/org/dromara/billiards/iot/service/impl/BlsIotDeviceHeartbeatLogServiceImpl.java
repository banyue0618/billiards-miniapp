package org.dromara.billiards.iot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.iot.domain.BlsIotDevice;
import org.dromara.billiards.iot.service.IBlsIotDeviceService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceHeartbeatLogBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceHeartbeatLogVo;
import org.dromara.billiards.iot.domain.BlsIotDeviceHeartbeatLog;
import org.dromara.billiards.iot.mapper.BlsIotDeviceHeartbeatLogMapper;
import org.dromara.billiards.iot.service.IBlsIotDeviceHeartbeatLogService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * IoT设备心跳记录Service业务层处理
 *
 * @author banyue
 * @date 2025-10-22
 */
@RequiredArgsConstructor
@Service
public class BlsIotDeviceHeartbeatLogServiceImpl extends ServiceImpl<BlsIotDeviceHeartbeatLogMapper, BlsIotDeviceHeartbeatLog> implements IBlsIotDeviceHeartbeatLogService {

    private final IBlsIotDeviceService iotDeviceService;

    /**
     * 查询IoT设备心跳记录
     *
     * @param id 主键
     * @return IoT设备心跳记录
     */
    @Override
    public BlsIotDeviceHeartbeatLogVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询IoT设备心跳记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return IoT设备心跳记录分页列表
     */
    @Override
    public TableDataInfo<BlsIotDeviceHeartbeatLogVo> queryPageList(BlsIotDeviceHeartbeatLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsIotDeviceHeartbeatLog> lqw = buildQueryWrapper(bo);
        Page<BlsIotDeviceHeartbeatLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的IoT设备心跳记录列表
     *
     * @param bo 查询条件
     * @return IoT设备心跳记录列表
     */
    @Override
    public List<BlsIotDeviceHeartbeatLogVo> queryList(BlsIotDeviceHeartbeatLogBo bo) {
        LambdaQueryWrapper<BlsIotDeviceHeartbeatLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsIotDeviceHeartbeatLog> buildQueryWrapper(BlsIotDeviceHeartbeatLogBo bo) {
        LambdaQueryWrapper<BlsIotDeviceHeartbeatLog> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsIotDeviceHeartbeatLog::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceCode()), BlsIotDeviceHeartbeatLog::getDeviceCode, bo.getDeviceCode());
        lqw.eq(StringUtils.isNotBlank(bo.getIpAddress()), BlsIotDeviceHeartbeatLog::getIpAddress, bo.getIpAddress());
        lqw.eq(StringUtils.isNotBlank(bo.getFirmwareVersion()), BlsIotDeviceHeartbeatLog::getFirmwareVersion, bo.getFirmwareVersion());
        lqw.eq(bo.getHeartbeatTime() != null, BlsIotDeviceHeartbeatLog::getHeartbeatTime, bo.getHeartbeatTime());
        lqw.eq(bo.getSignalStrength() != null, BlsIotDeviceHeartbeatLog::getSignalStrength, bo.getSignalStrength());
        return lqw;
    }

    /**
     * 新增IoT设备心跳记录
     *
     * @param bo IoT设备心跳记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsIotDeviceHeartbeatLogBo bo) {
        BlsIotDeviceHeartbeatLog add = MapstructUtils.convert(bo, BlsIotDeviceHeartbeatLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改IoT设备心跳记录
     *
     * @param bo IoT设备心跳记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsIotDeviceHeartbeatLogBo bo) {
        BlsIotDeviceHeartbeatLog update = MapstructUtils.convert(bo, BlsIotDeviceHeartbeatLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsIotDeviceHeartbeatLog entity){
        //TODO 做一些数据校验,如唯一约束
        BlsIotDevice blsIotDevice = iotDeviceService.queryByDeviceCode(entity.getDeviceCode());// 校验设备是否存在
        if (blsIotDevice == null) {
            throw BilliardsException.of("设备不存在，请先录入设备!");
        }
        entity.setTenantId(blsIotDevice.getTenantId());
        entity.setMerchantId(blsIotDevice.getMerchantId());
    }

    /**
     * 校验并批量删除IoT设备心跳记录信息
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
