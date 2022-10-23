package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.IncorrectIdException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTest {
    UserDto userDto;
    @MockBean
    private UserService userService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        userDto = new UserDto(1L, "Igor", "email@mail.ru");
    }

    @Test
    void getAllUsersTest() throws Exception {
        when(userService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(userService, times(1)).findAll();
    }

    @Test
    void getUserByIdTest() throws Exception {
        when(userService.find(1L))
                .thenReturn(new UserDto(1L, "Igor", "email@mail.ru"));
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Igor"));
        verify(userService, times(1)).find(1L);
    }

    @Test
    void getUserByIncorrectIdTest() throws Exception {
        when(userService.find(10L))
                .thenThrow(new NotFoundException("User not found"));
        mockMvc.perform(get("/users/10"))
                .andExpect(status().isNotFound());
        when(userService.find(-10L))
                .thenThrow(new IncorrectIdException(""));
        mockMvc.perform(get("/users/-10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserTest() throws Exception {
        when(userService.create(userDto)).thenReturn(userDto);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Igor"));
        verify(userService, times(1)).create(userDto);
    }

    @Test
    void updateUserByIdTest() throws Exception {
        when(userService.update(1L, userDto)).thenReturn(userDto);
        mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Igor"))
                .andExpect(jsonPath("$.id").value(1L));
        verify(userService, times(1)).update(1L, userDto);
    }

    @Test
    void deleteUserByIdTest() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(1L);
    }
}
