package ru.practicum.main_service.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.main_service.dto.user.UserDto;
import ru.practicum.main_service.exeptions.ConflictParametersException;
import ru.practicum.main_service.mapper.UserMapper;
import ru.practicum.main_service.model.UserEntity;
import ru.practicum.main_service.repository.UserRepository;
import ru.practicum.main_service.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Override
    public UserDto addNewUser(UserDto userDto) {
        if (Boolean.TRUE.equals(userRepository.existsByEmail(userDto.getEmail()))) {
            log.info("Юзер с такой почтой уже существует");
            throw new ConflictParametersException("Field: email must be unique");
        }
        UserEntity userEntity = userRepository.save(userMapper.fromUserDtoToUserEntity(userDto));
        log.info("сохранили в репозиторий нового пользователя под id: {}", userEntity.getId());
        return userMapper.fromUserEntityToUserDto(userEntity);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from, size);
        if (ids == null) {
            return userRepository.findAll(page).stream()
                    .map(userMapper::fromUserEntityToUserDto)
                    .collect(Collectors.toList());
        } else {
            return userRepository.findAllById(ids).stream()
                    .map(userMapper::fromUserEntityToUserDto)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
