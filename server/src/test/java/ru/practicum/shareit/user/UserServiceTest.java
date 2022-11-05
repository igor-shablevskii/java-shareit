package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    private UserServiceImpl userService;
    private UserStorage userStorage;
    private User user;
    private User user2;
    private UserDto userDto;


    @BeforeEach
    void beforeEach() {
        userStorage = mock(UserStorage.class);
        userService = new UserServiceImpl(userStorage);
        user = new User(1L, "name1", "email1@mail.ru");
        user2 = new User(2L, "nameDto1", "email2@mail.ru");
        userDto = new UserDto(1L, "nameDto1", "emaildto1@mail.ru");
    }

    @Test
    void getAllUsersTest() {
        List<User> users = new ArrayList<>(Collections.singletonList(user));
        when(userStorage.findAll()).thenReturn(users);
        List<UserDto> usersDto = userService.findAll();
        assertNotNull(usersDto);
        assertEquals(usersDto.get(0).getName(), "name1");
        assertEquals(1, usersDto.size());
    }

    @Test
    void getUserDtoByIdTest() {
        when(userStorage.findById(1L)).thenReturn(Optional.ofNullable(user));
        UserDto result = userService.find(1L);
        assertNotNull(result);
        assertEquals(result.getName(), "name1");
    }

    @Test
    void getUserDtoByWrongIdTest() {
        when(userStorage.findById(10L)).thenThrow(new NotFoundException("User not found"));
        NotFoundException ex = assertThrows(NotFoundException.class, () -> userService.find(10L));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void createUserTest() {
        when(userStorage.save(user)).thenReturn(user);
        UserDto result = userService.create(userDto);
        assertNotNull(result);
        assertEquals(result.getName(), "nameDto1");
    }

    @Test
    void getUserByIdTest() {
        when(userStorage.findById(1L)).thenReturn(Optional.ofNullable(user));
        UserDto result = userService.find(1L);
        assertNotNull(result);
        assertEquals(result.getName(), "name1");
    }

    @Test
    void updateUserByIdTest() {
        when(userStorage.save(user)).thenReturn(user2);
        when(userStorage.findById(1L)).thenReturn(Optional.ofNullable(user));
        UserDto result = userService.update(1L, userDto);
        assertNotNull(result);
        assertEquals(result.getName(), "nameDto1");
    }
}
