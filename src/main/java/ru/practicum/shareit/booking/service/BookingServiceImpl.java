package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
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
    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public BookingDto create(BookingDto bookingDto, Long userId) {
        User user = UserMapper.toUser(userService.find(userId));
        Item item = itemService.getItemById(bookingDto.getItemId());
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Item own this user");
        }
        if (item.getAvailable()) {
            Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, item, user));
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new UnavailableItemException("Item not available");
        }
    }

    @Override
    @Transactional
    public BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
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
            bookingRepository.save(booking);
            return BookingMapper.toBookingResponseDto(booking);
        } else {
            throw new NoAccessToSeeBookingException("No access for to view booking");
        }
    }

    @Override
    public BookingResponseDto find(Long userId, Long bookingId) {
        userService.find(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        Item item = itemService.getItemById(booking.getItem().getId());
        if (item.getOwner().getId().equals(userId) || booking.getBooker().getId().equals(userId)) {
            return BookingMapper.toBookingResponseDto(booking);
        } else {
            throw new NoAccessToSeeBookingException("No access for to view booking");
        }
    }

    @Override
    public List<BookingResponseDto> getAllBookingsByUserId(Long userId, BookingState state) {
        userService.find(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByBooker_Id(userId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findByBooker_IdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByBooker_IdAndEndBefore(userId,
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartAfter(userId,
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId,
                        BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBooker_IdAndStatus(userId,
                        BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                break;
        }
        return BookingMapper.toListBookingResponseDto(bookings);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsOfCurrentUserItems(Long userId, BookingState state) {
        userService.find(userId);
        List<Booking> bookings = new ArrayList<>();
        switch (state) {
            case ALL:
                bookings = bookingRepository.findAllByItem_Owner_Id(userId, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfter(userId,
                        LocalDateTime.now(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndBefore(userId,
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartAfter(userId,
                        LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId,
                        BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatus(userId,
                        BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                break;
        }
        return BookingMapper.toListBookingResponseDto(bookings);
    }
}