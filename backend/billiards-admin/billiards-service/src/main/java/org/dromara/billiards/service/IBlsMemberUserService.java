package org.dromara.billiards.service;

import org.dromara.billiards.domain.vo.BlsMemberUserVo;
import org.dromara.billiards.domain.bo.BlsMemberUserBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 会员用户Service接口
 *
 * @author banyue
 * @date 2025-06-17
 */
public interface IBlsMemberUserService {

    /**
     * 查询会员用户
     *
     * @param id 主键
     * @return 会员用户
     */
    BlsMemberUserVo queryById(String id);

    /**
     * 分页查询会员用户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员用户分页列表
     */
    TableDataInfo<BlsMemberUserVo> queryPageList(BlsMemberUserBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员用户列表
     *
     * @param bo 查询条件
     * @return 会员用户列表
     */
    List<BlsMemberUserVo> queryList(BlsMemberUserBo bo);

    /**
     * 新增会员用户
     *
     * @param bo 会员用户
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsMemberUserBo bo);

    /**
     * 修改会员用户
     *
     * @param bo 会员用户
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsMemberUserBo bo);

    /**
     * 校验并批量删除会员用户信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
