package org.dromara.billiards.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.dromara.billiards.domain.entity.BlsTableUsage;
import org.dromara.billiards.domain.vo.BlsTableUsageVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

/**
 * 桌台使用记录Mapper接口
 *
 * @author banyue
 * @date 2025-09-01
 */
@Mapper
public interface BlsTableUsageMapper extends BaseMapperPlus<BlsTableUsage, BlsTableUsageVo> {

}
