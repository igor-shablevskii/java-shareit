package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    @Transactional
    UserDto create(UserDto userDto);

    @Transactional
    UserDto update(Long userId, UserDto userDto);

    @Transactional
    UserDto delete(Long userId);

    List<UserDto> findAll();

    UserDto find(Long userId);
}