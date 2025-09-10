package org.dromara.billiards.service;

import org.dromara.billiards.domain.entity.BlsMemberLevelConfig;
import org.dromara.billiards.domain.vo.BlsMemberLevelConfigVo;
import org.dromara.billiards.domain.bo.BlsMemberLevelConfigBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 会员等级配置Service接口
 *
 * @author banyue
 * @date 2025-06-17
 */
public interface IBlsMemberLevelConfigService {

    /**
     * 查询会员等级配置
     *
     * @param id 主键
     * @return 会员等级配置
     */
    BlsMemberLevelConfigVo queryById(String id);

    /**
     * 分页查询会员等级配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员等级配置分页列表
     */
    TableDataInfo<BlsMemberLevelConfigVo> queryPageList(BlsMemberLevelConfigBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员等级配置列表
     *
     * @param bo 查询条件
     * @return 会员等级配置列表
     */
    List<BlsMemberLevelConfigVo> queryList(BlsMemberLevelConfigBo bo);

    /**
     * 新增会员等级配置
     *
     * @param bo 会员等级配置
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsMemberLevelConfigBo bo);

    /**
     * 修改会员等级配置
     *
     * @param bo 会员等级配置
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsMemberLevelConfigBo bo);

    /**
     * 校验并批量删除会员等级配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);

    /**
     *
     * @return 会员等级配置列表
     */
    List<BlsMemberLevelConfig> queryList();
}
