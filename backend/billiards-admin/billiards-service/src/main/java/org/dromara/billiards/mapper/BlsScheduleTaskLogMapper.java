package org.dromara.billiards.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.billiards.domain.entity.BlsScheduleTaskLog;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

@Mapper
public interface BlsScheduleTaskLogMapper extends BaseMapperPlus<BlsScheduleTaskLog, BlsScheduleTaskLog> {
}
