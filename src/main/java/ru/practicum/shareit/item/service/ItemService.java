package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    ItemDto delete(Long itemId);

    ItemDto find(Long itemId);

    List<ItemDto> findAllOwnerItems(Long userId);

    List<ItemDto> search(String text, Long userId);
}
