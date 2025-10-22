package org.dromara.billiards.iot.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.iot.domain.BlsIotControlLog;
import org.dromara.billiards.iot.domain.bo.BlsIotControlLogBo;
import org.dromara.billiards.iot.domain.vo.BlsIotControlLogVo;
import org.dromara.billiards.iot.mapper.BlsIotControlLogMapper;
import org.dromara.billiards.iot.service.IBlsIotControlLogService;
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
 * 设备控制日志（记录执行命令历史）Service业务层处理
 *
 * @author banyue
 * @date 2025-10-16
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsIotControlLogServiceImpl extends ServiceImpl<BlsIotControlLogMapper, BlsIotControlLog> implements IBlsIotControlLogService {

    /**
     * 查询设备控制日志（记录执行命令历史）
     *
     * @param id 主键
     * @return 设备控制日志（记录执行命令历史）
     */
    @Override
    public BlsIotControlLogVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备控制日志（记录执行命令历史）列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备控制日志（记录执行命令历史）分页列表
     */
    @Override
    public TableDataInfo<BlsIotControlLogVo> queryPageList(BlsIotControlLogBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsIotControlLog> lqw = buildQueryWrapper(bo);
        Page<BlsIotControlLogVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备控制日志（记录执行命令历史）列表
     *
     * @param bo 查询条件
     * @return 设备控制日志（记录执行命令历史）列表
     */
    @Override
    public List<BlsIotControlLogVo> queryList(BlsIotControlLogBo bo) {
        LambdaQueryWrapper<BlsIotControlLog> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsIotControlLog> buildQueryWrapper(BlsIotControlLogBo bo) {
        LambdaQueryWrapper<BlsIotControlLog> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsIotControlLog::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceCode()), BlsIotControlLog::getDeviceCode, bo.getDeviceCode());
        lqw.eq(StringUtils.isNotBlank(bo.getTriggerScene()), BlsIotControlLog::getTriggerScene, bo.getTriggerScene());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), BlsIotControlLog::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增设备控制日志（记录执行命令历史）
     *
     * @param bo 设备控制日志（记录执行命令历史）
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsIotControlLogBo bo) {
        BlsIotControlLog add = MapstructUtils.convert(bo, BlsIotControlLog.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备控制日志（记录执行命令历史）
     *
     * @param bo 设备控制日志（记录执行命令历史）
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsIotControlLogBo bo) {
        BlsIotControlLog update = MapstructUtils.convert(bo, BlsIotControlLog.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsIotControlLog entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除设备控制日志（记录执行命令历史）信息
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
