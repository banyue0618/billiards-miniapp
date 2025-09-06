package org.dromara.billiards.convert;

import org.dromara.billiards.domain.bo.PriceRuleDto;
import org.dromara.billiards.domain.entity.BlsPriceRule;
import org.dromara.billiards.domain.vo.PriceRuleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PriceRuleConvert {

    PriceRuleConvert INSTANCE = Mappers.getMapper(PriceRuleConvert.class);

    BlsPriceRule toEntity(PriceRuleDto dto);

    PriceRuleVO toVo(BlsPriceRule entity);

    List<PriceRuleVO> toVoList(List<BlsPriceRule> entityList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "merchantId", ignore = true) // Usually not changed via general update
    @Mapping(target = "createTime", ignore = true)
    void updateEntityFromDto(PriceRuleDto dto, @MappingTarget BlsPriceRule entity);
}
