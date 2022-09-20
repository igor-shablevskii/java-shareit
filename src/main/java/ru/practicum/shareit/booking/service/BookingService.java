package ru.practicum.shareit.booking.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

public interface BookingService {
    @Transactional
    BookingDto create(BookingDto bookingDto, Long userId);

    @Transactional
    BookingResponseDto approveBooking(Long userId, Long bookingId, Boolean isApproved);

    BookingResponseDto find(Long userId, Long bookingId);

    List<BookingResponseDto> getAllBookingsByUserId(Long userId, BookingState state);

    List<BookingResponseDto> getAllBookingsOfCurrentUserItems(Long userId, BookingState state);
}