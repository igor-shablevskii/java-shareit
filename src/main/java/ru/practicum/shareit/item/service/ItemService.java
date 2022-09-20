package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    @Transactional
    ItemDto create(Long id, ItemDto itemDto);

    @Transactional
    ItemDto update(Long id, ItemDto itemDto, Long itemId);

    ItemBookingDto getItemDtoById(Long userId, Long id);

    Item getItemById(Long itemId);

    List<ItemBookingDto> getAllItemsDtoOfUser(Long id);

    List<Item> getAllItemsOfUser(Long id);

    List<ItemDto> searchItemsByNameOrDescription(String text);

    @Transactional
    CommentDto createComment(Long id, Long itemId, CommentDto commentDto);
}
