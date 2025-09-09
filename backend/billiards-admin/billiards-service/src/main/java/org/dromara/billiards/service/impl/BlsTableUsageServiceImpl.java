package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.common.constant.OrderCompleteFlagEnum;
import org.dromara.billiards.domain.bo.BlsTableUsageBo;
import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.entity.BlsTableUsage;
import org.dromara.billiards.domain.vo.BlsTableUsageVo;
import org.dromara.billiards.mapper.BlsTableUsageMapper;
import org.dromara.billiards.service.IBlsTableUsageService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Collection;

/**
 * 桌台使用记录Service业务层处理
 *
 * @author banyue
 * @date 2025-09-01
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsTableUsageServiceImpl implements IBlsTableUsageService {

    private final BlsTableUsageMapper baseMapper;

    /**
     * 查询桌台使用记录
     *
     * @param id 主键
     * @return 桌台使用记录
     */
    @Override
    public BlsTableUsageVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询桌台使用记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 桌台使用记录分页列表
     */
    @Override
    public TableDataInfo<BlsTableUsageVo> queryPageList(BlsTableUsageBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsTableUsage> lqw = buildQueryWrapper(bo);
        Page<BlsTableUsageVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的桌台使用记录列表
     *
     * @param bo 查询条件
     * @return 桌台使用记录列表
     */
    @Override
    public List<BlsTableUsageVo> queryList(BlsTableUsageBo bo) {
        LambdaQueryWrapper<BlsTableUsage> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsTableUsage> buildQueryWrapper(BlsTableUsageBo bo) {
        LambdaQueryWrapper<BlsTableUsage> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsTableUsage::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getMerchantId()), BlsTableUsage::getMerchantId, bo.getMerchantId());
        lqw.eq(StringUtils.isNotBlank(bo.getStoreId()), BlsTableUsage::getStoreId, bo.getStoreId());
        lqw.eq(StringUtils.isNotBlank(bo.getTableId()), BlsTableUsage::getTableId, bo.getTableId());
        lqw.eq(StringUtils.isNotBlank(bo.getOrderId()), BlsTableUsage::getOrderId, bo.getOrderId());
        lqw.eq(bo.getUserId() != null, BlsTableUsage::getUserId, bo.getUserId());
        lqw.eq(bo.getStartTime() != null, BlsTableUsage::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, BlsTableUsage::getEndTime, bo.getEndTime());
        lqw.eq(bo.getDuration() != null, BlsTableUsage::getDuration, bo.getDuration());
        return lqw;
    }

    /**
     * 新增桌台使用记录
     *
     * @param bo 桌台使用记录
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsTableUsageBo bo) {
        BlsTableUsage add = MapstructUtils.convert(bo, BlsTableUsage.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改桌台使用记录
     *
     * @param bo 桌台使用记录
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsTableUsageBo bo) {
        BlsTableUsage update = MapstructUtils.convert(bo, BlsTableUsage.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsTableUsage entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除桌台使用记录信息
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

    @Override
    public Boolean saveTableUsage(BlsOrder blsOrder) {
        BlsTableUsage tableUsageBo = new BlsTableUsage();
        tableUsageBo.setStoreId(blsOrder.getStoreId());
        tableUsageBo.setTableId(blsOrder.getTableId());
        tableUsageBo.setOrderId(blsOrder.getId());
        tableUsageBo.setUserId(blsOrder.getUserId());
        tableUsageBo.setStartTime(blsOrder.getStartTime());
//        tableUsageBo.setEndTime(blsOrder.getEndTime());
//        tableUsageBo.setDuration(blsOrder.getDuration());
        if(OrderCompleteFlagEnum.TIMEOUT_END.getCode() == blsOrder.getCompleteFlag()){
            tableUsageBo.setCreateBy("system");
            tableUsageBo.setUpdateBy("system");
        }
        return baseMapper.insert(tableUsageBo) > 0;
    }

    @Override
    public Boolean trackTableUsage(BlsOrder blsOrder) {

        // 根据订单id找使用记录
        LambdaQueryWrapper<BlsTableUsage> lqw = Wrappers.lambdaQuery();
        lqw.eq(BlsTableUsage::getOrderId, blsOrder.getId());
        BlsTableUsage tableUsage = baseMapper.selectOne(lqw);
        tableUsage.setEndTime(blsOrder.getEndTime());
        tableUsage.setDuration(blsOrder.getDuration());
        return baseMapper.updateById(tableUsage) > 0;
    }
}
