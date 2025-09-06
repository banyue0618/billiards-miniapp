package org.dromara.billiards.service;

import org.dromara.billiards.domain.vo.BlsMemberBenefitVo;
import org.dromara.billiards.domain.bo.BlsMemberBenefitBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 会员权益Service接口
 *
 * @author banyue
 * @date 2025-06-17
 */
public interface IBlsMemberBenefitService {

    /**
     * 查询会员权益
     *
     * @param id 主键
     * @return 会员权益
     */
    BlsMemberBenefitVo queryById(String id);

    /**
     * 分页查询会员权益列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员权益分页列表
     */
    TableDataInfo<BlsMemberBenefitVo> queryPageList(BlsMemberBenefitBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员权益列表
     *
     * @param bo 查询条件
     * @return 会员权益列表
     */
    List<BlsMemberBenefitVo> queryList(BlsMemberBenefitBo bo);

    /**
     * 新增会员权益
     *
     * @param bo 会员权益
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsMemberBenefitBo bo);

    /**
     * 修改会员权益
     *
     * @param bo 会员权益
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsMemberBenefitBo bo);

    /**
     * 校验并批量删除会员权益信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
