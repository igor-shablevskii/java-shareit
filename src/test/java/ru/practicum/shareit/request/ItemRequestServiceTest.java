package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemRequestServiceTest {
    private ItemRequestStorage storage;
    private UserService userService;
    private ItemRepository itemRepository;
    private ItemRequestServiceImpl itemRequestService;
    private User user1;
    private Item item1;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        storage = mock(ItemRequestStorage.class);
        userService = mock(UserService.class);
        itemRepository = mock(ItemRepository.class);
        itemRequestService = new ItemRequestServiceImpl(storage, userService, itemRepository);
        user1 = new User(1L, "user1", "mail@mail.ru");
        item1 = new Item(1L, "item1", "descriptionItem1", true, user1, null);
        itemRequestDto = new ItemRequestDto(1L, "description1",
                LocalDateTime.now().withNano(0));
        itemRequest = new ItemRequest(1L, "description1", 1L, LocalDateTime.now().withNano(0));
        pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "created"));
    }

    @Test
    void createItemRequestTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user1));
        when(storage.save(itemRequest)).thenReturn(itemRequest);
        ItemRequestDto result = itemRequestService.createItemRequest(1L, itemRequestDto);

        assertNotNull(result);
        assertEquals(result.getDescription(), "description1");
    }

    @Test
    void getCurrentUserItemRequestsTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user1));
        when(storage.findAllByRequester(1L)).thenReturn(Collections.singletonList(itemRequest));
        when(itemRepository.findAllItemsByRequest(1L)).thenReturn(Collections.singletonList(item1));
        List<ItemRequestResponseDto> result = itemRequestService.getCurrentUserItemRequests(1L);

        assertNotNull(result);
        assertEquals(result.get(0).getDescription(), "description1");
    }

    @Test
    void getItemRequestByIdTest() {
        when(storage.findById(1L)).thenReturn(Optional.ofNullable(itemRequest));

        ItemRequest result = itemRequestService.getItemRequestById(1L);
        assertNotNull(result);
        assertEquals(result.getDescription(), "description1");
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequestById(10L));
    }

    @Test
    void getItemRequestsFromAnotherUsersTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user1));
        Page<ItemRequest> page = new PageImpl<>(Collections.singletonList(itemRequest));
        when(storage.findItemRequestsFromAnotherUsers(1L, pageRequest))
                .thenReturn(page);
        when(itemRepository.findAllItemsByRequest(1L)).thenReturn(Collections.singletonList(item1));
        List<ItemRequestResponseDto> result = itemRequestService.getItemRequestsFromAnotherUsers(1L, pageRequest);

        assertNotNull(result);
        assertEquals(result.get(0).getDescription(), "description1");
    }

    @Test
    void getItemRequestResponseByIdTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user1));
        when(itemRepository.findAllItemsByRequest(1L)).thenReturn(Collections.singletonList(item1));
        when(storage.findById(1L)).thenReturn(Optional.ofNullable(itemRequest));
        ItemRequestResponseDto result = itemRequestService.getItemRequestById(1L, 1L);

        assertNotNull(result);
        assertEquals(result.getDescription(), "description1");
    }
}