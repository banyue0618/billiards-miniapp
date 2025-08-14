package org.dromara.billiards.service;

import org.dromara.billiards.domain.vo.BlsPointsRuleVo;
import org.dromara.billiards.domain.bo.BlsPointsRuleBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 积分规则Service接口
 *
 * @author banyue
 * @date 2025-06-17
 */
public interface IBlsPointsRuleService {

    /**
     * 查询积分规则
     *
     * @param id 主键
     * @return 积分规则
     */
    BlsPointsRuleVo queryById(String id);

    /**
     * 分页查询积分规则列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 积分规则分页列表
     */
    TableDataInfo<BlsPointsRuleVo> queryPageList(BlsPointsRuleBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的积分规则列表
     *
     * @param bo 查询条件
     * @return 积分规则列表
     */
    List<BlsPointsRuleVo> queryList(BlsPointsRuleBo bo);

    /**
     * 新增积分规则
     *
     * @param bo 积分规则
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsPointsRuleBo bo);

    /**
     * 修改积分规则
     *
     * @param bo 积分规则
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsPointsRuleBo bo);

    /**
     * 校验并批量删除积分规则信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
