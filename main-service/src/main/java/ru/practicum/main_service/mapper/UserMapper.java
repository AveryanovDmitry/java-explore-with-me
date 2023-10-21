package ru.practicum.main_service.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main_service.dto.user.UserDto;
import ru.practicum.main_service.model.UserEntity;

@Mapper(componentModel = "Spring")
public interface UserMapper {
    UserDto fromUserEntityToUserDto(UserEntity userEntity);

    UserEntity fromUserDtoToUserEntity(UserDto newUserDto);
}
