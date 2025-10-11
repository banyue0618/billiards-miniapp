package org.dromara.billiards.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.billiards.domain.entity.BlsEventOutbox;
import org.dromara.billiards.domain.vo.BlsEventOutboxVo;
import org.dromara.billiards.domain.bo.BlsEventOutboxBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 本地消息(Outbox)Service接口
 *
 * @author banyue
 * @date 2025-09-15
 */
public interface IBlsEventOutboxService extends IService<BlsEventOutbox> {

    /**
     * 查询本地消息(Outbox)
     *
     * @param id 主键
     * @return 本地消息(Outbox)
     */
    BlsEventOutboxVo queryById(String id);

    /**
     * 分页查询本地消息(Outbox)列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 本地消息(Outbox)分页列表
     */
    TableDataInfo<BlsEventOutboxVo> queryPageList(BlsEventOutboxBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的本地消息(Outbox)列表
     *
     * @param bo 查询条件
     * @return 本地消息(Outbox)列表
     */
    List<BlsEventOutboxVo> queryList(BlsEventOutboxBo bo);

    /**
     * 新增本地消息(Outbox)
     *
     * @param bo 本地消息(Outbox)
     * @return 是否新增成功
     */
    Boolean insertByBo(BlsEventOutboxBo bo);

    /**
     * 修改本地消息(Outbox)
     *
     * @param bo 本地消息(Outbox)
     * @return 是否修改成功
     */
    Boolean updateByBo(BlsEventOutboxBo bo);

    /**
     * 校验并批量删除本地消息(Outbox)信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<String> ids, Boolean isValid);


    /**
     * 查询待处理的消息记录
     * @return
     */
    List<BlsEventOutbox> queryPendingMessages();

    /**
     * 尝试锁定消息（使用乐观锁 CAS 防止多实例重复处理）
     * @param id 消息ID
     * @param expectedStatus 期望的当前状态
     * @return 是否成功锁定
     */
    boolean tryLockMessage(String id, Long expectedStatus);
}
