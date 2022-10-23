package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.error.NoAccessToSeeBookingException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.IncorrectStateException;
import ru.practicum.shareit.error.UnavailableItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
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

public class BookingServiceTest {
    private BookingRepository bookingRepository;
    private ItemService itemService;
    private UserService userService;
    private BookingServiceImpl bookingService;
    private BookingDto bookingDto;
    private Booking booking;
    private Item item;
    private Item itemNotAvailable;
    private User user;
    private User user2;
    private User user3;
    private PageRequest pageRequest;

    @BeforeEach
    void beforeEach() {
        bookingRepository = mock(BookingRepository.class);
        itemService = mock(ItemService.class);
        userService = mock(UserService.class);
        bookingService = new BookingServiceImpl(bookingRepository, userService, itemService);
        user = new User(1L, "name1", "email@mail.ru");
        user2 = new User(2L, "name2", "email2@mail.ru");
        user3 = new User(3L, "name3", "email3@mail.ru");
        item = new Item(1L, "item1", "description1", true, user, null);
        itemNotAvailable = new Item(2L, "item2", "description2", false, user, null);
        bookingDto = new BookingDto(1L, 1L,
                LocalDateTime.of(2023, 5, 23, 12, 0), LocalDateTime.MAX);
        pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "start"));
        booking = new Booking(1L, LocalDateTime.of(2021, 5, 23, 12, 0),
                LocalDateTime.MAX, item, user2, BookingStatus.APPROVED);
    }

    @Test
    void createNewBookingByItemOwnerTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user));
        when(itemService.getItemById(1L)).thenReturn(item);
        when(bookingRepository.save(booking)).thenReturn(booking);

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDto, 1L));
    }

    @Test
    void createNewBookingTest() {
        when(userService.find(2L)).thenReturn(UserMapper.toUserDto(user2));
        when(itemService.getItemById(1L)).thenReturn(item);
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingResponseDto result = bookingService.create(bookingDto, 2L);

        assertNotNull(result);
        assertEquals(result.getItem().getId(), 1L);
    }

    @Test
    void createNewBookingWithItemNotAvailableTest() {
        bookingDto.setItemId(2L);
        when(userService.find(2L)).thenReturn(UserMapper.toUserDto(user2));
        when(itemService.getItemById(2L)).thenReturn(itemNotAvailable);
        when(bookingRepository.save(booking)).thenReturn(booking);

        assertThrows(UnavailableItemException.class, () -> bookingService.create(bookingDto, 2L));
    }

    @Test
    void approveBookingTest() {
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        when(itemService.getItemById(1L)).thenReturn(item);
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user));
        when(userService.find(2L)).thenReturn(UserMapper.toUserDto(user2));
        when(bookingRepository.save(booking)).thenReturn(booking);
        BookingResponseDto result = bookingService.approveBooking(1L, 1L, true);

        assertEquals(result.getStatus(), BookingStatus.APPROVED);

        booking.setStatus(BookingStatus.APPROVED);
        assertThrows(IncorrectStateException.class,
                () -> bookingService.approveBooking(1L, 1L, true));

        booking.setStatus(BookingStatus.REJECTED);
        assertThrows(IncorrectStateException.class,
                () -> bookingService.approveBooking(1L, 1L, false));

        booking.setStatus(BookingStatus.WAITING);
        result = bookingService.approveBooking(1L, 1L, false);
        assertEquals(result.getStatus(), BookingStatus.REJECTED);

        assertThrows(NoAccessToSeeBookingException.class,
                () -> bookingService.approveBooking(2L, 1L, false));
    }

    @Test
    void getBookingByIdTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        when(itemService.getItemById(1L)).thenReturn(item);
        BookingResponseDto result = bookingService.find(1L, 1L);

        assertEquals(result.getStatus(), BookingStatus.APPROVED);
        assertThrows(NotFoundException.class, () -> bookingService.find(1L, 10L));
        assertThrows(NoAccessToSeeBookingException.class, () -> bookingService.find(3L, 1L));
    }

    @Test
    void getAllBookingsByUserIdTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByBookerId(1L, pageRequest)).thenReturn(Collections.singletonList(booking));
        List<BookingResponseDto> result = bookingService.getAllBookingsByUserId(1L, BookingState.ALL, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);

        when(bookingRepository.findByBookerIdAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsByUserId(1L, BookingState.CURRENT, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);

        when(bookingRepository.findByBookerIdAndEndBefore(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsByUserId(1L, BookingState.PAST, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);

        when(bookingRepository.findByBookerIdAndStartAfter(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsByUserId(1L, BookingState.FUTURE, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);

        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findByBookerIdAndStatus(1L, BookingStatus.WAITING, pageRequest))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsByUserId(1L, BookingState.WAITING, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);

        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findByBookerIdAndStatus(1L, BookingStatus.REJECTED, pageRequest))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsByUserId(1L, BookingState.REJECTED, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.REJECTED);
    }

    @Test
    void getAllBookingsOfCurrentUserItemsTest() {
        when(userService.find(1L)).thenReturn(UserMapper.toUserDto(user));
        when(bookingRepository.findAllByItemOwnerId(1L, pageRequest)).thenReturn(Collections.singletonList(booking));
        List<BookingResponseDto> result = bookingService.getAllBookingsOfCurrentUserItems(1L, BookingState.ALL, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);

        when(bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfter(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsOfCurrentUserItems(1L, BookingState.CURRENT, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);

        when(bookingRepository.findByItemOwnerIdAndEndBefore(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsOfCurrentUserItems(1L, BookingState.PAST, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);

        when(bookingRepository.findByItemOwnerIdAndStartAfter(any(), any(), any()))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsOfCurrentUserItems(1L, BookingState.FUTURE, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.APPROVED);

        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findByItemOwnerIdAndStatus(1L, BookingStatus.WAITING, pageRequest))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsOfCurrentUserItems(1L, BookingState.WAITING, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.WAITING);

        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findByItemOwnerIdAndStatus(1L, BookingStatus.REJECTED, pageRequest))
                .thenReturn(Collections.singletonList(booking));
        result = bookingService.getAllBookingsOfCurrentUserItems(1L, BookingState.REJECTED, pageRequest);
        assertEquals(result.get(0).getStatus(), BookingStatus.REJECTED);
    }
}