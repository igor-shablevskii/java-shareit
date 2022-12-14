package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto create(Long id, ItemDto itemDto);

    ItemDto update(Long id, ItemDto itemDto, Long itemId);

    ItemBookingDto getItemDtoById(Long userId, Long id);

    Item getItemById(Long itemId);

    List<ItemBookingDto> getAllItemsDtoOfUser(Long id, PageRequest pageRequest);

    List<Item> getAllItemsOfUser(Long id, PageRequest pageRequest);

    List<ItemDto> searchItemsByNameOrDescription(String text, PageRequest pageRequest);

    CommentDto createComment(Long id, Long itemId, CommentDto commentDto);
}
