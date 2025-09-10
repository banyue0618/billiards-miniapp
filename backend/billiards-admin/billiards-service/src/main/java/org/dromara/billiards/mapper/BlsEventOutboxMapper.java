package org.dromara.billiards.mapper;

import org.dromara.billiards.domain.entity.BlsEventOutbox;
import org.dromara.billiards.domain.vo.BlsEventOutboxVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 本地消息(Outbox)Mapper接口
 *
 * @author banyue
 * @date 2025-09-15
 */
public interface BlsEventOutboxMapper extends BaseMapperPlus<BlsEventOutbox, BlsEventOutboxVo> {

}
