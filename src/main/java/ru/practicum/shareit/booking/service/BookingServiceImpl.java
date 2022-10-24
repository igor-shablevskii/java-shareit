package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.error.IncorrectStateException;
import ru.practicum.shareit.error.NoAccessToSeeBookingException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.UnavailableItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingResponseDto create(BookingDto bookingDto, Long userId) {
        User user = UserMapper.toUser(userService.find(userId));
        Item item = itemService.getItemById(bookingDto.getItemId());
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item own this user");
        }
        if (item.getAvailable()) {
            Booking booking = bookingStorage.save(BookingMapper.toBooking(bookingDto, item, user));
            return BookingMapper.toBookingResponseDto(booking);
        } else {
            throw new UnavailableItemException("Item not available");
        }
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        Item item = itemService.getItemById(booking.getItem().getId());
        userService.find(userId);
        if (item.getOwner().getId().equals(userId)) {
            if (isApproved) {
                if (!booking.getStatus().equals(BookingStatus.APPROVED)) {
                    booking.setStatus(BookingStatus.APPROVED);
                } else {
                    throw new IncorrectStateException("Already Approved");
                }
            } else {
                if (!booking.getStatus().equals(BookingStatus.REJECTED)) {
                    booking.setStatus(BookingStatus.REJECTED);
                } else {
                    throw new IncorrectStateException("Already Rejected");
                }
            }
            bookingStorage.save(booking);
            return BookingMapper.toBookingResponseDto(booking);
        } else {
            throw new NoAccessToSeeBookingException("No access for to view booking");
        }
    }

    @Override
    public BookingResponseDto find(Long userId, Long bookingId) {
        userService.find(userId);
        Booking booking = bookingStorage.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        Item item = itemService.getItemById(booking.getItem().getId());
        if (item.getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingResponseDto(booking);
        } else {
            throw new NoAccessToSeeBookingException("No access for to view booking");
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByUserId(Long userId, BookingState state, PageRequest pageRequest) {
        userService.find(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByBookerId(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingStorage.findByBookerIdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingStorage.findByBookerIdAndEndBefore(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingStorage.findByBookerIdAndStartAfter(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingStorage.findByBookerIdAndStatus(userId,
                        BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingStorage.findByBookerIdAndStatus(userId,
                        BookingStatus.REJECTED, pageRequest);
                break;
        }
        return BookingMapper.toListBookingResponseDto(bookings);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfCurrentUserItems(Long userId, BookingState state,
                                                                     PageRequest pageRequest) {
        userService.find(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingStorage.findAllByItemOwnerId(userId, pageRequest);
                break;
            case CURRENT:
                bookings = bookingStorage.findByItemOwnerIdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), pageRequest);
                break;
            case PAST:
                bookings = bookingStorage.findByItemOwnerIdAndEndBefore(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case FUTURE:
                bookings = bookingStorage.findByItemOwnerIdAndStartAfter(userId,
                        LocalDateTime.now(), pageRequest);
                break;
            case WAITING:
                bookings = bookingStorage.findByItemOwnerIdAndStatus(userId,
                        BookingStatus.WAITING, pageRequest);
                break;
            case REJECTED:
                bookings = bookingStorage.findByItemOwnerIdAndStatus(userId,
                        BookingStatus.REJECTED, pageRequest);
                break;
        }
        return BookingMapper.toListBookingResponseDto(bookings);
    }
}