package org.dromara.billiards.convert;

import org.dromara.billiards.domain.bo.PriceRuleDto;
import org.dromara.billiards.domain.entity.PriceRule;
import org.dromara.billiards.domain.vo.PriceRuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceRuleConvert {

    PriceRuleConvert INSTANCE = Mappers.getMapper(PriceRuleConvert.class);

    PriceRule toEntity(PriceRuleDto dto);

    PriceRuleVO toVo(PriceRule entity);

    List<PriceRuleVO> toVoList(List<PriceRule> entityList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "merchantId", ignore = true) // Usually not changed via general update
    @Mapping(target = "createTime", ignore = true)
    void updateEntityFromDto(PriceRuleDto dto, @MappingTarget PriceRule entity);
}
