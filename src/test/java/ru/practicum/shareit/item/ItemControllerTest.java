package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
@AutoConfigureMockMvc
public class ItemControllerTest {
    @MockBean
    private ItemServiceImpl itemService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private ItemDto itemDto;
    private ItemBookingDto itemBookingDto;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "item1", "description1", true, 1L);
        itemBookingDto = new ItemBookingDto(
                1L,
                "item1",
                "description1",
                true,
                null,
                null,
                null
        );
    }

    @Test
    void createItemTest() throws Exception {
        when(itemService.create(1L, itemDto)).thenReturn(itemDto);
        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("item1"));
        verify(itemService, times(1)).create(1L, itemDto);
    }

    @Test
    void getItemByIdTest() throws Exception {
        when(itemService.getItemDtoById(1L, 1L)).thenReturn(itemBookingDto);
        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("item1"));
        verify(itemService, times(1)).getItemDtoById(1L, 1L);
    }

    @Test
    void getItemByIncorrectIdTest() throws Exception {
        when(itemService.getItemDtoById(1L, 10L)).thenThrow(new NotFoundException(""));
        mockMvc.perform(get("/items/10")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateItemTest() throws Exception {
        when(itemService.update(1L, itemDto, 1L)).thenReturn(itemDto);
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("item1"));
        verify(itemService, times(1)).update(1L, itemDto, 1L);
    }

    @Test
    void updateItemByWrongUserTest() throws Exception {
        when(itemService.update(3L, itemDto, 1L)).thenThrow(new NotFoundException(""));
        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 3L)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsDtoOfUser() throws Exception {
        when(itemService.getAllItemsDtoOfUser(1L, PageRequest.of(0, 2)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/items?from=0&size=2")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1))
                .getAllItemsDtoOfUser(1L, PageRequest.of(0, 2));
    }

    @Test
    void searchItemsByNameOrDescriptionTest() throws Exception {
        when(itemService.searchItemsByNameOrDescription("qwerty", PageRequest.of(0, 2)))
                .thenReturn(Collections.emptyList());
        mockMvc.perform(get("/items/search?text=qwerty&from=0&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(itemService, times(1))
                .searchItemsByNameOrDescription("qwerty", PageRequest.of(0, 2));
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDto dto = new CommentDto(1L, "comment1", "Pavel", LocalDateTime.now());
        when(itemService.createComment(1L, 1L, dto)).thenReturn(dto);
        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("comment1"));
        verify(itemService, times(1))
                .createComment(1L, 1L, dto);
    }
}