package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@AutoConfigureMockMvc
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestServiceImpl itemRequestService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private ItemRequestDto itemRequestDto;
    private ItemRequestResponseDto itemRequestResponseDto;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        itemRequestDto = new ItemRequestDto(1L, "description1",
                LocalDateTime.now().withNano(0));
        ItemDto itemDto = new ItemDto(1L, "item1", "description1", true, 1L);
        itemRequestResponseDto = new ItemRequestResponseDto(1L, "descriptionResponse",
                LocalDateTime.now().withNano(0), null);
        pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "created"));
    }

    @Test
    void createItemRequestTest() throws Exception {
        when(itemRequestService.createItemRequest(1L, itemRequestDto)).thenReturn(itemRequestDto);
        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("description1"));
        verify(itemRequestService, times(1)).createItemRequest(1L, itemRequestDto);
    }

    @Test
    void getCurrentUserItemRequestsTest() throws Exception {
        when(itemRequestService.getCurrentUserItemRequests(1L)).thenReturn(Collections.singletonList(itemRequestResponseDto));
        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value("descriptionResponse"));
        verify(itemRequestService, times(1)).getCurrentUserItemRequests(1L);
    }

    @Test
    void getItemRequestsFromAnotherUsersTest() throws Exception {
        when(itemRequestService.getItemRequestsFromAnotherUsers(2L, pageRequest)).thenReturn(Collections.singletonList(itemRequestResponseDto));
        mockMvc.perform(get("/requests/all?from=0&size=2")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].description").value("descriptionResponse"));
        verify(itemRequestService, times(1)).getItemRequestsFromAnotherUsers(2L, pageRequest);
    }

    @Test
    void getItemRequestByIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(1L, 1L)).thenReturn(itemRequestResponseDto);
        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.description").value("descriptionResponse"));
        verify(itemRequestService, times(1)).getItemRequestById(1L, 1L);
    }

    @Test
    void getItemRequestByIncorrectIdTest() throws Exception {
        when(itemRequestService.getItemRequestById(1L, 10L)).thenThrow(new NotFoundException(""));
        mockMvc.perform(get("/requests/10")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}