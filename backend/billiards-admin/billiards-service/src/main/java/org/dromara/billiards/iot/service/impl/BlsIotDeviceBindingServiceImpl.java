package org.dromara.billiards.iot.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.exception.BilliardsException;
import org.dromara.billiards.iot.domain.BlsIotDevice;
import org.dromara.billiards.iot.domain.BlsIotDeviceBinding;
import org.dromara.billiards.iot.domain.bo.BlsIotDeviceBindingBo;
import org.dromara.billiards.iot.domain.vo.BlsIotDeviceBindingVo;
import org.dromara.billiards.iot.mapper.BlsIotDeviceBindingMapper;
import org.dromara.billiards.iot.service.IBlsIotDeviceBindingService;
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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 设备业务绑定（定义场景与设备动作映射）Service业务层处理
 *
 * @author banyue
 * @date 2025-10-16
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsIotDeviceBindingServiceImpl extends ServiceImpl<BlsIotDeviceBindingMapper, BlsIotDeviceBinding> implements IBlsIotDeviceBindingService {

    private final IBlsIotDeviceService iotDeviceService;

    /**
     * 查询设备业务绑定（定义场景与设备动作映射）
     *
     * @param id 主键
     * @return 设备业务绑定（定义场景与设备动作映射）
     */
    @Override
    public BlsIotDeviceBindingVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询设备业务绑定（定义场景与设备动作映射）列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 设备业务绑定（定义场景与设备动作映射）分页列表
     */
    @Override
    public TableDataInfo<BlsIotDeviceBindingVo> queryPageList(BlsIotDeviceBindingBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsIotDeviceBinding> lqw = buildQueryWrapper(bo);
        Page<BlsIotDeviceBindingVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的设备业务绑定（定义场景与设备动作映射）列表
     *
     * @param bo 查询条件
     * @return 设备业务绑定（定义场景与设备动作映射）列表
     */
    @Override
    public List<BlsIotDeviceBindingVo> queryList(BlsIotDeviceBindingBo bo) {
        LambdaQueryWrapper<BlsIotDeviceBinding> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsIotDeviceBinding> buildQueryWrapper(BlsIotDeviceBindingBo bo) {
        LambdaQueryWrapper<BlsIotDeviceBinding> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsIotDeviceBinding::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getScene()), BlsIotDeviceBinding::getScene, bo.getScene());
        lqw.eq(StringUtils.isNotBlank(bo.getDeviceCode()), BlsIotDeviceBinding::getDeviceCode, bo.getDeviceCode());
        lqw.eq(StringUtils.isNotBlank(bo.getCommand()), BlsIotDeviceBinding::getCommand, bo.getCommand());
        lqw.eq(bo.getEnabled() != null, BlsIotDeviceBinding::getEnabled, bo.getEnabled());
        return lqw;
    }

    /**
     * 新增设备业务绑定（定义场景与设备动作映射）
     *
     * @param bo 设备业务绑定（定义场景与设备动作映射）
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsIotDeviceBindingBo bo) {
        BlsIotDeviceBinding add = MapstructUtils.convert(bo, BlsIotDeviceBinding.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改设备业务绑定（定义场景与设备动作映射）
     *
     * @param bo 设备业务绑定（定义场景与设备动作映射）
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsIotDeviceBindingBo bo) {
        BlsIotDeviceBinding update = MapstructUtils.convert(bo, BlsIotDeviceBinding.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsIotDeviceBinding entity){

        BlsIotDevice blsIotDevice = iotDeviceService.queryByDeviceCode(entity.getDeviceCode());// 校验设备是否存在
        if (blsIotDevice == null) {
            throw BilliardsException.of("设备不存在，请先录入设备!");
        }

        // 设备已绑定到其它桌台
        Long n1 = baseMapper.selectCount(Wrappers.<BlsIotDeviceBinding>lambdaQuery()
            .eq(BlsIotDeviceBinding::getDeviceCode, entity.getDeviceCode())
            .ne(BlsIotDeviceBinding::getTableId, entity.getTableId())
            .ne(entity.getId()!=null, BlsIotDeviceBinding::getId, entity.getId()));
        if (n1 > 0) throw BilliardsException.of("该设备已绑定到其它桌台");

        // 设备+桌台+场景+控制命令 具有唯一性
        Long n2 = baseMapper.selectCount(Wrappers.<BlsIotDeviceBinding>lambdaQuery()
            .eq(BlsIotDeviceBinding::getTableId, entity.getTableId())
            .eq(BlsIotDeviceBinding::getScene, entity.getScene())
            .eq(BlsIotDeviceBinding::getCommand, entity.getCommand())
            .ne(entity.getId()!=null, BlsIotDeviceBinding::getId, entity.getId()));
        if (n2 > 0) throw BilliardsException.of("同一桌台同一场景下命令不可重复");

        // 单台设备只能绑定一个桌台，但是会有多个场景（开台、关台），也就是会存在多条绑定记录，同一个桌台、同一种场景，执行顺序不能重复、控制命令不能重复
        Long n3 = baseMapper.selectCount(Wrappers.<BlsIotDeviceBinding>lambdaQuery()
            .eq(BlsIotDeviceBinding::getTableId, entity.getTableId())
            .eq(BlsIotDeviceBinding::getScene, entity.getScene())
            .eq(BlsIotDeviceBinding::getExecuteOrder, entity.getExecuteOrder())
            .ne(entity.getId()!=null, BlsIotDeviceBinding::getId, entity.getId()));
        if (n3 > 0) throw BilliardsException.of("执行顺序不能重复");

        String topic = blsIotDevice.getType() + "/" + entity.getDeviceCode();
        entity.setMqttTopic("/billiards/" + topic + "/command");
    }

    /**
     * 校验并批量删除设备业务绑定（定义场景与设备动作映射）信息
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
    public List<BlsIotDeviceBinding> selectByTableIdAndScene(String tableId, String scene) {
        LambdaQueryWrapper<BlsIotDeviceBinding> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlsIotDeviceBinding::getTableId, tableId)
            .eq(BlsIotDeviceBinding::getScene, scene)
            .eq(BlsIotDeviceBinding::getEnabled, 1);
        List<BlsIotDeviceBinding> bindings = baseMapper.selectList(lqw);
        bindings.sort(Comparator.comparingLong(b -> b.getExecuteOrder() == null ? 0L : b.getExecuteOrder()));
        return bindings;
    }
}
