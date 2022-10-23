package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import ru.practicum.shareit.item.dto.comment.dto.CommentDto;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ItemBookingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BookingDto {
        Long id;
        LocalDateTime start;
        LocalDateTime end;
        Long bookerId;
    }
}
