package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
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
import ru.practicum.shareit.error.IncorrectStateException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long id,
                                         @Validated @RequestBody BookingDto bookingDto) {
        return bookingClient.createNewBooking(bookingDto, id);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader("X-Sharer-User-Id") Long id,
                                             @PathVariable Long bookingId,
                                             @RequestParam Boolean approved) {
        return bookingClient.approveBooking(id, bookingId, approved);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsByUserId(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        try {
            BookingState bookingState = BookingState.valueOf(state);
            return bookingClient.getAllBookingsByUserId(id, bookingState, from, size);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException("Unknown state: " + state);
        }

    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader("X-Sharer-User-Id") Long id,
                                                 @PathVariable Long bookingId) {
        return bookingClient.getBookingById(id, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOfCurrentUserItems(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @RequestParam(required = false, defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        try {
            BookingState bookingState = BookingState.valueOf(state);
            return bookingClient.getAllBookingsOfCurrentUserItems(id, bookingState, from, size);
        } catch (IllegalArgumentException e) {
            throw new IncorrectStateException("Unknown state: " + state);
        }
    }
}