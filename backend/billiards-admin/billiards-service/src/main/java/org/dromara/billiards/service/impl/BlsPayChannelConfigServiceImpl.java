package org.dromara.billiards.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.billiards.common.constant.BilliardsConstants;
import org.dromara.billiards.domain.bo.BlsPayChannelConfigBo;
import org.dromara.billiards.domain.entity.BlsPayChannelConfig;
import org.dromara.billiards.domain.vo.BlsPayChannelConfigVo;
import org.dromara.billiards.mapper.BlsPayChannelConfigMapper;
import org.dromara.billiards.service.IBlsPayChannelConfigService;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.dromara.common.tenant.helper.MerchantHolder;
import org.dromara.common.tenant.helper.TenantHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.dromara.billiards.security.MerchantQueryHelper;
import org.dromara.billiards.security.MerchantWriteGuard;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 支付服务商配置(门店>商户>租户)Service业务层处理
 *
 * @author banyue
 * @date 2025-08-28
 */
@RequiredArgsConstructor
@Service
@DS(BilliardsConstants.DS_BILLIARDS_PLATFORM)
public class BlsPayChannelConfigServiceImpl extends ServiceImpl<BlsPayChannelConfigMapper, BlsPayChannelConfig> implements IBlsPayChannelConfigService {

    @Value("${billiards.wechat.appid}")
    private String appid;

    /**
     * 查询支付服务商配置(门店>商户>租户)
     *
     * @param id 主键
     * @return 支付服务商配置(门店>商户>租户)
     */
    @Override
    public BlsPayChannelConfigVo queryById(String id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询支付服务商配置(门店>商户>租户)列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 支付服务商配置(门店>商户>租户)分页列表
     */
    @Override
    public TableDataInfo<BlsPayChannelConfigVo> queryPageList(BlsPayChannelConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<BlsPayChannelConfig> lqw = buildQueryWrapper(bo);
        MerchantQueryHelper.apply(lqw, BlsPayChannelConfig::getMerchantId);
        Page<BlsPayChannelConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的支付服务商配置(门店>商户>租户)列表
     *
     * @param bo 查询条件
     * @return 支付服务商配置(门店>商户>租户)列表
     */
    @Override
    public List<BlsPayChannelConfigVo> queryList(BlsPayChannelConfigBo bo) {
        LambdaQueryWrapper<BlsPayChannelConfig> lqw = buildQueryWrapper(bo);
        MerchantQueryHelper.apply(lqw, BlsPayChannelConfig::getMerchantId);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<BlsPayChannelConfig> buildQueryWrapper(BlsPayChannelConfigBo bo) {
        LambdaQueryWrapper<BlsPayChannelConfig> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(BlsPayChannelConfig::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getMerchantId()), BlsPayChannelConfig::getMerchantId, bo.getMerchantId());
        lqw.eq(StringUtils.isNotBlank(bo.getStoreId()), BlsPayChannelConfig::getStoreId, bo.getStoreId());
        lqw.eq(StringUtils.isNotBlank(bo.getAppId()), BlsPayChannelConfig::getAppId, bo.getAppId());
        lqw.eq(StringUtils.isNotBlank(bo.getSubMchId()), BlsPayChannelConfig::getSubMchId, bo.getSubMchId());
        lqw.eq(bo.getStatus() != null, BlsPayChannelConfig::getStatus, bo.getStatus());
        lqw.eq(bo.getIsDelete() != null, BlsPayChannelConfig::getIsDelete, bo.getIsDelete());
        return lqw;
    }

    /**
     * 新增支付服务商配置(门店>商户>租户)
     *
     * @param bo 支付服务商配置(门店>商户>租户)
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(BlsPayChannelConfigBo bo) {
        BlsPayChannelConfig add = MapstructUtils.convert(bo, BlsPayChannelConfig.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改支付服务商配置(门店>商户>租户)
     *
     * @param bo 支付服务商配置(门店>商户>租户)
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(BlsPayChannelConfigBo bo) {
        BlsPayChannelConfig update = MapstructUtils.convert(bo, BlsPayChannelConfig.class);
        MerchantWriteGuard.assertWritable(update.getMerchantId());
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(BlsPayChannelConfig entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除支付服务商配置(门店>商户>租户)信息
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
        // 简单防护：逐条校验商户
        List<BlsPayChannelConfig> list = baseMapper.selectBatchIds(ids);
        for (BlsPayChannelConfig e : list) {
            MerchantWriteGuard.assertWritable(e.getMerchantId());
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    @Override
    public String selectMerchantIdByStoreId(String storeId) {
        return baseMapper.selectMerchantIdByStoreId(storeId, appid, TenantHelper.getTenantId(), MerchantHolder.get());
    }
}
