package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemServiceTest {
    private ItemServiceImpl itemService;
    private ItemStorage itemStorage;
    private BookingStorage bookingStorage;
    private ItemRequestStorage itemRequestStorage;
    private CommentRepository commentRepository;
    private UserService userService;
    private Item item;
    private User user;
    private ItemDto itemDto;
    private Booking booking;

    @BeforeEach
    void beforeEach() {
        itemStorage = mock(ItemStorage.class);
        bookingStorage = mock(BookingStorage.class);
        commentRepository = mock(CommentRepository.class);
        ItemRequestService itemRequestService = mock(ItemRequestService.class);
        userService = mock(UserService.class);
        itemService = new ItemServiceImpl(itemStorage, bookingStorage, commentRepository,
                userService, itemRequestStorage);
        user = new User(1L, "Tor", "Tor@mail.ru");
        itemDto = new ItemDto(1L, "item1", "description", true, null);
        item = new Item(1L, "item1", "description", true, user, null);
        booking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED);
    }

    @Test
    void createItemTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user));
        when(itemStorage.save(item)).thenReturn(item);
        ItemDto result = itemService.create(1L, itemDto);

        assertNotNull(result);
        assertEquals(result.getName(), "item1");
    }

    @Test
    void getItemDtoByIdTest() {
        when(itemStorage.findLastBooking(1L, 1L, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(Collections.singletonList(new Booking(1L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED)));
        when(itemStorage.findNextBooking(1L, 1L, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(Collections.singletonList(new Booking(2L, LocalDateTime.now(), LocalDateTime.now(), item, user, BookingStatus.APPROVED)));
        when(commentRepository.findComments(1L)).thenReturn(Collections.emptyList());
        when(itemStorage.findById(1L)).thenReturn(Optional.ofNullable(item));
        ItemBookingDto result = itemService.getItemDtoById(1L, 1L);

        assertNotNull(result);
        assertEquals(result.getName(), "item1");
    }

    @Test
    void getAllItemsDtoOfUserTest() {
        when(itemStorage.findLastBooking(1L, 1L, LocalDateTime.now(),
                Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(Collections.singletonList(new Booking(1L, LocalDateTime.now(), LocalDateTime.now(),
                        item, user, BookingStatus.APPROVED)));
        when(itemStorage.findNextBooking(1L, 1L, LocalDateTime.now(),
                Sort.by(Sort.Direction.DESC, "start")))
                .thenReturn(Collections.singletonList(new Booking(2L, LocalDateTime.now(), LocalDateTime.now(),
                        item, user, BookingStatus.APPROVED)));
        when(commentRepository.findComments(1L)).thenReturn(Collections.emptyList());
        when(itemStorage.findByOwnerId(1L, PageRequest.of(0, 2))).thenReturn(Collections.singletonList(item));
        List<ItemBookingDto> result = itemService.getAllItemsDtoOfUser(1L, PageRequest.of(0, 2));

        assertNotNull(result);
        assertEquals(result.get(0).getName(), "item1");
    }

    @Test
    void searchItemsByNameOrDescriptionWithEmptyTextTest() {
        List<ItemDto> result = itemService.searchItemsByNameOrDescription("", PageRequest.of(0, 2));
        assertTrue(result.isEmpty());
    }

    @Test
    void searchItemsByNameOrDescriptionWithTextTest() {
        when(itemStorage.searchItemsByNameOrDescription("Text", PageRequest.of(0, 2)))
                .thenReturn(Collections.singletonList(item));
        List<ItemDto> result = itemService.searchItemsByNameOrDescription("Text", PageRequest.of(0, 2));
        assertNotNull(result);
        assertEquals(result.get(0).getName(), "item1");
    }

    @Test
    void createCommentTest() {
        when(bookingStorage.findBooking(any(), any(), any()))
                .thenReturn(booking);
        when(itemStorage.findById(1L)).thenReturn(Optional.ofNullable(item));
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user));
        when(commentRepository.save(any())).thenReturn(new Comment(1L, "comment1", item, user, LocalDateTime.now()));
        CommentDto dto = new CommentDto(1L, "comment1", "Igor", LocalDateTime.now());
        CommentDto result = itemService.createComment(1L, 1L, dto);

        assertNotNull(result);
        assertEquals(result.getText(), "comment1");
    }

    @Test
    void checkIdTest() {
        when(userService.find(-1L)).thenThrow(new NotFoundException("User nor found"));
        assertThrows(NotFoundException.class, () -> itemService.create(-1L, itemDto));
    }

    @Test
    void itemNotFoundExceptionTest() {
        assertThrows(NotFoundException.class, () -> itemService.getItemById(10L));
    }

    @Test
    void incorrectOwnerExceptionTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user));
        when(itemStorage.findById(1L)).thenReturn(Optional.ofNullable(item));

        assertThrows(NotFoundException.class, () -> itemService.update(2L, itemDto, 1L));
    }
}
