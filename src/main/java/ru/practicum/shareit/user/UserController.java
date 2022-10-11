package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto create(@RequestBody @Validated({Create.class}) UserDto useDto) {
        UserDto createdUserDto = userService.create(useDto);
        log.info("Created {}", createdUserDto);
        return createdUserDto;
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody @Validated({Update.class}) UserDto userDto) {
        UserDto updatedUserDto = userService.update(userId, userDto);
        log.info("Updated {}", updatedUserDto);
        return updatedUserDto;
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        UserDto removedUser = userService.delete(userId);
        log.info("Delete {}", removedUser);
    }

    @GetMapping("/{userId}")
    public UserDto find(@PathVariable Long userId) {
        UserDto foundUser = userService.find(userId);
        log.info("Found {} by id={}", foundUser, userId);
        return foundUser;
    }

    @GetMapping
    public List<UserDto> findAll() {
        List<UserDto> listFoundUsers = userService.findAll();
        log.info("Found all users {}", listFoundUsers);
        return listFoundUsers;
    }
}