package org.dromara.billiards.service;

import org.dromara.billiards.domain.vo.BlsMemberPointsRecordVo;
import org.dromara.billiards.domain.bo.BlsMemberPointsRecordBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 会员积分记录Service接口
 *
 * @author banyue
 * @date 2025-06-17
 */
public interface IBlsMemberPointsRecordService {

    /**
     * 查询会员积分记录
     *
     * @param id 主键
     * @return 会员积分记录
     */
    BlsMemberPointsRecordVo queryById(String id);

    /**
     * 分页查询会员积分记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员积分记录分页列表
     */
    TableDataInfo<BlsMemberPointsRecordVo> queryPageList(BlsMemberPointsRecordBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员积分记录列表
     *
     * @param bo 查询条件
     * @return 会员积分记录列表
     */
    List<BlsMemberPointsRecordVo> queryList(BlsMemberPointsRecordBo bo);

    /**
     * 新增会员积分记录
     *
     * @param bo 会员积分记录
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsMemberPointsRecordBo bo);

    /**
     * 修改会员积分记录
     *
     * @param bo 会员积分记录
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsMemberPointsRecordBo bo);

    /**
     * 校验并批量删除会员积分记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
