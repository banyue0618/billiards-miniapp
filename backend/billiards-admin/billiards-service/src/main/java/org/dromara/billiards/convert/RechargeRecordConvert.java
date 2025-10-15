package org.dromara.billiards.convert;

import org.dromara.billiards.domain.entity.BlsPayRecord;
import org.dromara.billiards.domain.vo.RechargeRecordVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 充值记录转换器
 *
 * @author banyue
 */
@Mapper
public interface RechargeRecordConvert {

    RechargeRecordConvert INSTANCE = Mappers.getMapper(RechargeRecordConvert.class);

    /**
     * 充值记录实体转VO
     */
    @Mapping(target = "merchantName", ignore = true)
    @Mapping(target = "refundStatus", ignore = true)
    @Mapping(target = "refundAmount", ignore = true)
    RechargeRecordVO toVo(BlsPayRecord entity);
}
