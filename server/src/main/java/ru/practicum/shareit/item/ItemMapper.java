package ru.practicum.shareit.item;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null);
    }

    public static Item toItem(ItemDto itemDto, User user, ItemRequest itemRequest) {
        return new Item(
                itemDto.getId(),
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                user,
                itemRequest);
    }

    public static ItemBookingDto toItemBookingDto(Item item,
                                              ItemBookingDto.BookingDto lastBookingDto,
                                              ItemBookingDto.BookingDto nextBookingDto,
                                              List<CommentDto> comments) {
        return new ItemBookingDto(item.getId(), item.getName(), item.getDescription(), item.getAvailable(),
                lastBookingDto, nextBookingDto, comments);
    }

    public static List<CommentDto> toListCommentDto(List<Comment> list) {
        return list.stream().map(ItemMapper::toCommentDto).collect(Collectors.toList());
    }

    public static ItemBookingDto.BookingDto toLastNextBookingDto(List<Booking> list) {
        if (list != null && !list.isEmpty()) {
            return new ItemBookingDto.BookingDto(list.get(0).getId(),
                    list.get(0).getStart(),
                    list.get(0).getEnd(),
                    list.get(0).getBooker().getId());
        } else {
            return null;
        }
    }

    public static List<ItemDto> toListItemDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return new Comment(commentDto.getId(), commentDto.getText(), item, user, LocalDateTime.now());
    }

    public static CommentDto toCommentDto(Comment comment) {
        return new CommentDto(comment.getId(), comment.getText(), comment.getAuthor().getName(), comment.getCreated());
    }
}