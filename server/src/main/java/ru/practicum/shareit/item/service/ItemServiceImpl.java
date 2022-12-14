package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.storage.BookingStorage;
import ru.practicum.shareit.error.IncorrectIdException;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final BookingStorage bookingStorage;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestStorage itemRequestStorage;

    @Override
    @Transactional
    public ItemDto create(Long id, ItemDto itemDto) {
        ItemRequest itemRequest = null;
        User user = UserMapper.toUser(userService.find(id));
        if (itemDto.getRequestId() != null) {
            itemRequest = itemRequestStorage.getReferenceById(itemDto.getRequestId());
        }
        Item item = itemStorage.save(ItemMapper.toItem(itemDto, user, itemRequest));
        return ItemMapper.toItemDto(item);
    }

    @Override
    @Transactional
    public ItemDto update(Long id, ItemDto itemDto, Long itemId) {
        userService.find(id);
        Item item = itemStorage.findByIdAndOwnerId(itemId, id)
                .orElseThrow(() -> new NotFoundException(String.format("Item id = %d not found for " +
                        "user id = %d", itemId, id)));
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    @Override
    public ItemBookingDto getItemDtoById(Long userId, Long itemId) {
        ItemBookingDto.BookingDto lastBookingDto = ItemMapper.toLastNextBookingDto(itemStorage.findLastBooking(userId,
                itemId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
        ItemBookingDto.BookingDto nextBookingDto = ItemMapper.toLastNextBookingDto(itemStorage.findNextBooking(userId,
                itemId, LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
        List<Comment> comments = commentRepository.findComments(itemId);
        return ItemMapper.toItemBookingDto(getItemById(itemId), lastBookingDto, nextBookingDto,
                ItemMapper.toListCommentDto(comments));
    }

    @Override
    public Item getItemById(Long id) {
        return itemStorage.findById(id).orElseThrow(() -> new NotFoundException("Item not found"));
    }

    @Override
    public List<ItemBookingDto> getAllItemsDtoOfUser(Long id, PageRequest pageRequest) {
        List<Item> items = getAllItemsOfUser(id, pageRequest);
        List<ItemBookingDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemBookingDto.BookingDto lastBookingDto = ItemMapper.toLastNextBookingDto(itemStorage.findLastBooking(id,
                    item.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            ItemBookingDto.BookingDto nextBookingDto = ItemMapper.toLastNextBookingDto(itemStorage.findNextBooking(id,
                    item.getId(), LocalDateTime.now(), Sort.by(Sort.Direction.DESC, "start")));
            List<Comment> comments = commentRepository.findComments(item.getId());
            result.add(ItemMapper.toItemBookingDto(item, lastBookingDto, nextBookingDto,
                    ItemMapper.toListCommentDto(comments)));
        }
        return result.stream().sorted(Comparator.comparing(ItemBookingDto::getId)).collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItemsOfUser(Long id, PageRequest pageRequest) {
        return itemStorage.findByOwnerId(id, pageRequest);
    }

    @Override
    public List<ItemDto> searchItemsByNameOrDescription(String text, PageRequest pageRequest) {
        if (!text.equals("")) {
            return ItemMapper.toListItemDto(itemStorage.searchItemsByNameOrDescription(text, pageRequest));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional
    public CommentDto createComment(Long id, Long itemId, CommentDto commentDto) {
        Booking booking = bookingStorage.findBooking(id, itemId, LocalDateTime.now());
        Item item = getItemById(itemId);
        User user = UserMapper.toUser(userService.find(id));
        if (booking != null) {
            Comment comment = commentRepository.save(ItemMapper.toComment(commentDto, item, user));
            return ItemMapper.toCommentDto(comment);
        } else {
            throw new IncorrectIdException("Comment incorrect");
        }
    }
}