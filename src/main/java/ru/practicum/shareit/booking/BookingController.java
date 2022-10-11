package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long id,
                             @Validated @RequestBody BookingDto bookingDto) {
        return bookingService.create(bookingDto, id);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingService.approveBooking(id, bookingId, approved);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state);
        if (bookingState == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookingService.getAllBookingsByUserId(id, bookingState);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto find(@RequestHeader("X-Sharer-User-Id") Long id, @PathVariable Long bookingId) {
        return bookingService.find(id, bookingId);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsOfCurrentUserItems(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(required = false, defaultValue = "ALL") String state) {
        BookingState bookingState = BookingState.from(state);
        if (bookingState == null) {
            throw new IllegalArgumentException("Unknown state: " + state);
        }
        return bookingService.getAllBookingsOfCurrentUserItems(id, bookingState);
    }
}