package org.dromara.billiards.service;

import org.dromara.billiards.domain.bo.BlsTableUsageBo;
import org.dromara.billiards.domain.entity.BlsOrder;
import org.dromara.billiards.domain.vo.BlsTableUsageVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * 桌台使用记录Service接口
 *
 * @author banyue
 * @date 2025-09-01
 */
public interface IBlsTableUsageService {

    /**
     * 查询桌台使用记录
     *
     * @param id 主键
     * @return 桌台使用记录
     */
    BlsTableUsageVo queryById(String id);

    /**
     * 分页查询桌台使用记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 桌台使用记录分页列表
     */
    TableDataInfo<BlsTableUsageVo> queryPageList(BlsTableUsageBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的桌台使用记录列表
     *
     * @param bo 查询条件
     * @return 桌台使用记录列表
     */
    List<BlsTableUsageVo> queryList(BlsTableUsageBo bo);

    /**
     * 新增桌台使用记录
     *
     * @param bo 桌台使用记录
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsTableUsageBo bo);

    /**
     * 修改桌台使用记录
     *
     * @param bo 桌台使用记录
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsTableUsageBo bo);

    /**
     * 校验并批量删除桌台使用记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     * 保存桌台使用记录
     * @param blsOrder
     * @return
     */
    Boolean saveTableUsage(BlsOrder blsOrder);


    /**
     * 记录结束时间与时长
     * @param
     * @return
     */
    Boolean trackTableUsage(BlsOrder blsOrder);
}
