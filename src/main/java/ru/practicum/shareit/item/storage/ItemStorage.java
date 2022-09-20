package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Item item);

    Item update(Item item);

    Item delete(Long itemId);

    Item find(Long itemId);

    List<Item> search(String text);

    boolean existsById(Long itemId);

    List<Item> findAllOwnerItems(Long userId);
}