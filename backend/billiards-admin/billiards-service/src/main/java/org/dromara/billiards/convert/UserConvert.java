package org.dromara.billiards.convert;

import org.dromara.billiards.domain.bo.UserUpdateDto;
import org.dromara.billiards.domain.entity.User;
import org.dromara.billiards.domain.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserConvert {

    UserConvert INSTANCE = Mappers.getMapper(UserConvert.class);

    UserVO toVo(User entity);
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget User entity);

}
