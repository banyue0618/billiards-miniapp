package org.dromara.system.service;

import org.dromara.system.domain.vo.SysMerchantVo;
import org.dromara.system.domain.bo.SysMerchantBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 商户Service接口
 *
 * @author banyue
 * @date 2025-09-01
 */
public interface ISysMerchantService {

    /**
     * 查询商户
     *
     * @param id 主键
     * @return 商户
     */
    SysMerchantVo queryById(Long id);

    /**
     * 分页查询商户列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 商户分页列表
     */
    TableDataInfo<SysMerchantVo> queryPageList(SysMerchantBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的商户列表
     *
     * @param bo 查询条件
     * @return 商户列表
     */
    List<SysMerchantVo> queryList(SysMerchantBo bo);

    /**
     * 新增商户
     *
     * @param bo 商户
     * @return 是否新增成功
     */
    Boolean insertByBo(SysMerchantBo bo);

    /**
     * 修改商户
     *
     * @param bo 商户
     * @return 是否修改成功
     */
    Boolean updateByBo(SysMerchantBo bo);

    /**
     * 校验并批量删除商户信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
