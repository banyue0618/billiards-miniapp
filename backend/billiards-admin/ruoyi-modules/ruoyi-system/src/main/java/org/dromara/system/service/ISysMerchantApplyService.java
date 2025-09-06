package org.dromara.system.service;

import org.dromara.system.domain.vo.SysMerchantApplyVo;
import org.dromara.system.domain.bo.SysMerchantApplyBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 商户注册申请Service接口
 *
 * @author banyue
 * @date 2025-09-01
 */
public interface ISysMerchantApplyService {

    /**
     * 查询商户注册申请
     *
     * @param id 主键
     * @return 商户注册申请
     */
    SysMerchantApplyVo queryById(Long id);

    /**
     * 分页查询商户注册申请列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商户注册申请分页列表
     */
    TableDataInfo<SysMerchantApplyVo> queryPageList(SysMerchantApplyBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的商户注册申请列表
     *
     * @param bo 查询条件
     * @return 商户注册申请列表
     */
    List<SysMerchantApplyVo> queryList(SysMerchantApplyBo bo);

    /**
     * 新增商户注册申请
     *
     * @param bo 商户注册申请
     * @return 是否新增成功
     */
    Boolean insertByBo(SysMerchantApplyBo bo);

    /**
     * 修改商户注册申请
     *
     * @param bo 商户注册申请
     * @return 是否修改成功
     */
    Boolean updateByBo(SysMerchantApplyBo bo);

    /**
     * 校验并批量删除商户注册申请信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 审批通过：在对应租户下创建商户并更新申请状态
     */
    void approve(Long id, SysMerchantApplyBo bo);

    /**
     * 审批驳回：仅更新申请状态与原因
     */
    void reject(Long id, String reason);
}
