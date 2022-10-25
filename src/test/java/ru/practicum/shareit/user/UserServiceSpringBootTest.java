package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserServiceSpringBootTest {
    @Autowired
    private UserServiceImpl userService;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = makeUserDto("Tor", "email@email.com");
    }

    @Test
    void createUserTest() {
        UserDto result = userService.create(userDto);

        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));
        assertThat(result.getName(), equalTo(userDto.getName()));
    }

    @Test
    void updateUserTest() {
        UserDto dto = makeUserDto("updatedName", "updatedEmail@mail.ru");
        userService.create(userDto);
        UserDto result = userService.update(1L, dto);

        assertThat(result.getId(), notNullValue());
        assertThat(result.getEmail(), equalTo(dto.getEmail()));
        assertThat(result.getName(), equalTo(dto.getName()));
    }

    @Test
    void deleteUserByIdTest() {
        UserDto dto = userService.create(userDto);

        assertThat(dto.getId(), equalTo(1L));
        userService.delete(1L);
        assertThrows(NotFoundException.class, () -> userService.find(1L));
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto dto = new UserDto();
        dto.setEmail(email);
        dto.setName(name);
        return dto;
    }
}