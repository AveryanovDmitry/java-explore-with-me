package ru.practicum.main_service.controller.adminController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_service.dto.user.UserDto;
import ru.practicum.main_service.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
@Slf4j
@Validated
public class AdminUserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto addNewUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получил запрос на создание нового юзера: {}", userDto);
        return userService.addNewUser(userDto);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                            @RequestParam(required = false, defaultValue = "0") Integer from,
                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info("Обрабатываю запрос на получение списка юзеров");
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUserById(id);
    }
}
