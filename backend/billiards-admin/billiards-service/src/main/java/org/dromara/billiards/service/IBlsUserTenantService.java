package org.dromara.billiards.service;

import org.dromara.billiards.domain.bo.BlsUserTenantBo;
import org.dromara.billiards.domain.entity.BlsUser;
import org.dromara.billiards.domain.vo.BlsUserTenantVo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 平台用户-租户映射Service接口
 *
 * @author banyue
 * @date 2025-08-28
 */
public interface IBlsUserTenantService {

    /**
     * 查询平台用户-租户映射
     *
     * @param id 主键
     * @return 平台用户-租户映射
     */
    BlsUserTenantVo queryById(Long id);

    /**
     * 分页查询平台用户-租户映射列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 平台用户-租户映射分页列表
     */
    TableDataInfo<BlsUserTenantVo> queryPageList(BlsUserTenantBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的平台用户-租户映射列表
     *
     * @param bo 查询条件
     * @return 平台用户-租户映射列表
     */
    List<BlsUserTenantVo> queryList(BlsUserTenantBo bo);

    /**
     * 新增平台用户-租户映射
     *
     * @param bo 平台用户-租户映射
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsUserTenantBo bo);

    /**
     * 修改平台用户-租户映射
     *
     * @param bo 平台用户-租户映射
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsUserTenantBo bo);

    /**
     * 校验并批量删除平台用户-租户映射信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 保存用户租户记录
     * @param blsUser 用户信息
     * @return 是否保存成功
     */
    Boolean saveUserTenantRecord(BlsUser blsUser);
}
