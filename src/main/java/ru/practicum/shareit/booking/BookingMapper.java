package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd());

    }

    public static Booking toBooking(BookingDto bookingDto, Item item, User user) {
        return new Booking(
                bookingDto.getId(),
                bookingDto.getStart(),
                bookingDto.getEnd(),
                item,
                user,
                BookingStatus.WAITING
        );
    }

    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        return new BookingResponseDto(
                booking.getId(),
                booking.getStatus(),
                booking.getStart(),
                booking.getEnd(),
                new BookingResponseDto.Item(booking.getItem().getId(), booking.getItem().getName()),
                new BookingResponseDto.User(booking.getBooker().getId())
        );
    }

    public static List<BookingResponseDto> toListBookingResponseDto(List<Booking> list) {
        return list.stream().map(BookingMapper::toBookingResponseDto).collect(Collectors.toList());
    }
}