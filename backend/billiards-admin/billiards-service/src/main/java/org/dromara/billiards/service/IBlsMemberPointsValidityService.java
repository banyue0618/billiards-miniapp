package org.dromara.billiards.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.domain.entity.BlsMemberPointsValidity;
import org.dromara.billiards.domain.vo.BlsMemberPointsValidityVo;
import org.dromara.billiards.domain.bo.BlsMemberPointsValidityBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 积分有效期Service接口
 *
 * @author banyue
 * @date 2025-06-17
 */
public interface IBlsMemberPointsValidityService extends IService<BlsMemberPointsValidity> {

    /**
     * 查询积分有效期
     *
     * @param id 主键
     * @return 积分有效期
     */
    BlsMemberPointsValidityVo queryById(String id);

    /**
     * 分页查询积分有效期列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 积分有效期分页列表
     */
    TableDataInfo<BlsMemberPointsValidityVo> queryPageList(BlsMemberPointsValidityBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的积分有效期列表
     *
     * @param bo 查询条件
     * @return 积分有效期列表
     */
    List<BlsMemberPointsValidityVo> queryList(BlsMemberPointsValidityBo bo);

    /**
     * 新增积分有效期
     *
     * @param bo 积分有效期
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsMemberPointsValidityBo bo);

    /**
     * 修改积分有效期
     *
     * @param bo 积分有效期
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsMemberPointsValidityBo bo);

    /**
     * 校验并批量删除积分有效期信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
