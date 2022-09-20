package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ConflictException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(UserStorage storage) {
        this.userStorage = storage;
    }

    @Override
    public UserDto create(UserDto userDto) {
        checkExistsEmail(userDto.getEmail());
        User user = UserMapper.toUser(userDto);
        userStorage.create(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        checkExistsUser(userId);
        checkExistsEmail(userDto.getEmail());
        User user = userStorage.find(userId);
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        userStorage.update(user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto delete(Long userId) {
        return UserMapper.toUserDto(userStorage.delete(userId));
    }

    @Override
    public UserDto find(Long userId) {
        checkExistsUser(userId);
        return UserMapper.toUserDto(userStorage.find(userId));
    }

    @Override
    public List<UserDto> findAll() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    private void checkExistsUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
    }

    private void checkExistsEmail(String email) {
        if (userStorage.checkEmail(email)) {
            throw new ConflictException(String.format("Email %s already exists", email));
        }
    }
}