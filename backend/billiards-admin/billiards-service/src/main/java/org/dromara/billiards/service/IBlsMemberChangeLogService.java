package org.dromara.billiards.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.domain.entity.BlsMemberChangeLog;
import org.dromara.billiards.domain.vo.BlsMemberChangeLogVo;
import org.dromara.billiards.domain.bo.BlsMemberChangeLogBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 会员变更记录Service接口
 *
 * @author banyue
 * @date 2025-09-15
 */
public interface IBlsMemberChangeLogService extends IService<BlsMemberChangeLog> {

    /**
     * 查询会员变更记录
     *
     * @param id 主键
     * @return 会员变更记录
     */
    BlsMemberChangeLogVo queryById(String id);

    /**
     * 分页查询会员变更记录列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 会员变更记录分页列表
     */
    TableDataInfo<BlsMemberChangeLogVo> queryPageList(BlsMemberChangeLogBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的会员变更记录列表
     *
     * @param bo 查询条件
     * @return 会员变更记录列表
     */
    List<BlsMemberChangeLogVo> queryList(BlsMemberChangeLogBo bo);

    /**
     * 新增会员变更记录
     *
     * @param bo 会员变更记录
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsMemberChangeLogBo bo);

    /**
     * 修改会员变更记录
     *
     * @param bo 会员变更记录
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsMemberChangeLogBo bo);

    /**
     * 校验并批量删除会员变更记录信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);
}
