package org.dromara.billiards.iot.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.iot.domain.BlsIotDevice;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceVo;
import org.dromara.billiards.iot.mapper.BlsIotDeviceMapper;
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

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * IoT设备Service业务层处理
 *
 * @author banyue
 * @date 2025-10-16
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsIotDeviceServiceImpl extends ServiceImpl<BlsIotDeviceMapper, BlsIotDevice> implements IBlsIotDeviceService {

    /**
     * 查询IoT设备
     *
     * @param id 主键
     * @return IoT设备
     */
    @Override
    public BlsIotDeviceVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询IoT设备列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return IoT设备分页列表
     */
    @Override
    public TableDataInfo<BlsIotDeviceVo> queryPageList(BlsIotDeviceBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsIotDevice> lqw = buildQueryWrapper(bo);
        Page<BlsIotDeviceVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的IoT设备列表
     *
     * @param bo 查询条件
     * @return IoT设备列表
     */
    @Override
    public List<BlsIotDeviceVo> queryList(BlsIotDeviceBo bo) {
        LambdaQueryWrapper<BlsIotDevice> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsIotDevice> buildQueryWrapper(BlsIotDeviceBo bo) {
        LambdaQueryWrapper<BlsIotDevice> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsIotDevice::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getCode()), BlsIotDevice::getCode, bo.getCode());
        lqw.like(StringUtils.isNotBlank(bo.getName()), BlsIotDevice::getName, bo.getName());
        lqw.eq(StringUtils.isNotBlank(bo.getType()), BlsIotDevice::getType, bo.getType());
        lqw.eq(StringUtils.isNotBlank(bo.getProtocol()), BlsIotDevice::getProtocol, bo.getProtocol());
        lqw.eq(StringUtils.isNotBlank(bo.getProtocolConfig()), BlsIotDevice::getProtocolConfig, bo.getProtocolConfig());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BlsIotDevice::getStatus, bo.getStatus());
        lqw.eq(bo.getLastHeartbeat() != null, BlsIotDevice::getLastHeartbeat, bo.getLastHeartbeat());
        return lqw;
    }

    /**
     * 新增IoT设备
     *
     * @param bo IoT设备
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsIotDeviceBo bo) {
        BlsIotDevice add = MapstructUtils.convert(bo, BlsIotDevice.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改IoT设备
     *
     * @param bo IoT设备
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsIotDeviceBo bo) {
        BlsIotDevice update = MapstructUtils.convert(bo, BlsIotDevice.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsIotDevice entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除IoT设备信息
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
    public BlsIotDevice queryByDeviceCode(String deviceCode) {
        if (StringUtils.isNotBlank(deviceCode)) {
            return baseMapper.selectOne(Wrappers.<BlsIotDevice>lambdaQuery()
                .eq(BlsIotDevice::getCode, deviceCode));
        }
        return null;
    }
}
