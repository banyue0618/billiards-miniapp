package org.dromara.billiards.service;

import org.dromara.billiards.domain.bo.BlsPayChannelConfigBo;
import org.dromara.billiards.domain.vo.BlsPayChannelConfigVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 支付服务商配置(门店>商户>租户)Service接口
 *
 * @author banyue
 * @date 2025-08-28
 */
public interface IBlsPayChannelConfigService {

    /**
     * 查询支付服务商配置(门店>商户>租户)
     *
     * @param id 主键
     * @return 支付服务商配置(门店>商户>租户)
     */
    BlsPayChannelConfigVo queryById(String id);

    /**
     * 分页查询支付服务商配置(门店>商户>租户)列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 支付服务商配置(门店>商户>租户)分页列表
     */
    TableDataInfo<BlsPayChannelConfigVo> queryPageList(BlsPayChannelConfigBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的支付服务商配置(门店>商户>租户)列表
     *
     * @param bo 查询条件
     * @return 支付服务商配置(门店>商户>租户)列表
     */
    List<BlsPayChannelConfigVo> queryList(BlsPayChannelConfigBo bo);

    /**
     * 新增支付服务商配置(门店>商户>租户)
     *
     * @param bo 支付服务商配置(门店>商户>租户)
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsPayChannelConfigBo bo);

    /**
     * 修改支付服务商配置(门店>商户>租户)
     *
     * @param bo 支付服务商配置(门店>商户>租户)
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsPayChannelConfigBo bo);

    /**
     * 校验并批量删除支付服务商配置(门店>商户>租户)信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     *  根据门店id查询商户id
     */
    String selectMerchantIdByStoreId(String storeId);
}
