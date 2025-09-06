package org.dromara.system.service;

import org.dromara.system.domain.vo.SysTenantApplyVo;
import org.dromara.system.domain.bo.SysTenantApplyBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 租户注册申请Service接口
 *
 * @author banyue
 * @date 2025-09-01
 */
public interface ISysTenantApplyService {

    /**
     * 查询租户注册申请
     *
     * @param id 主键
     * @return 租户注册申请
     */
    SysTenantApplyVo queryById(Long id);

    /**
     * 分页查询租户注册申请列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 租户注册申请分页列表
     */
    TableDataInfo<SysTenantApplyVo> queryPageList(SysTenantApplyBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的租户注册申请列表
     *
     * @param bo 查询条件
     * @return 租户注册申请列表
     */
    List<SysTenantApplyVo> queryList(SysTenantApplyBo bo);

    /**
     * 新增租户注册申请
     *
     * @param bo 租户注册申请
     * @return 是否新增成功
     */
    Boolean insertByBo(SysTenantApplyBo bo);

    /**
     * 修改租户注册申请
     *
     * @param bo 租户注册申请
     * @return 是否修改成功
     */
    Boolean updateByBo(SysTenantApplyBo bo);

    /**
     * 校验并批量删除租户注册申请信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 审批通过：创建租户并更新申请状态
     */
    void approve(Long id, SysTenantApplyBo bo);

    /**
     * 审批驳回：仅更新申请状态与原因
     */
    void reject(Long id, String reason);
}
