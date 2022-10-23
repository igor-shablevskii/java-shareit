package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UserMapperTest {
    User user = new User(1L, "name1", "email1@mail.ru");
    User user2 = new User(2L, "name2", "email2@mail.ru");
    UserDto userDto = new UserDto(1L, "nameDto1", "emailDto1@mail.ru");

    @Test
    void mapToUserDtoTest() {
        UserDto result = UserMapper.toUserDto(user);
        assertEquals(result.getName(), "name1");
    }

    @Test
    void mapToUserTest() {
        User result = UserMapper.toUser(userDto);
        assertEquals(result.getName(), "nameDto1");
    }

    @Test
    void mapToListUserDtoTest() {
        List<User> list = new ArrayList<>();
        list.add(user);
        list.add(user2);
        List<UserDto> result = UserMapper.toUserDtoList(list);

        assertEquals(result.get(0).getName(), "name1");
        assertEquals(result.get(1).getName(), "name2");
    }
}