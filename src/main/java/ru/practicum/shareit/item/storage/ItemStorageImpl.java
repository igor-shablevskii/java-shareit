package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private final Map<Long, Item> items;

    public ItemStorageImpl() {
        this.items = new HashMap<>();
    }

    @Override
    public Item create(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item delete(Long itemId) {
        return items.remove(itemId);
    }

    @Override
    public Item find(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> findAllOwnerItems(Long userId) {
        return items.values()
                .stream().filter(i -> i.getOwnerId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return new ArrayList<>();
        }
        return items.values().stream().filter(i -> (i.getName().toLowerCase().contains(text.toLowerCase())
                || i.getDescription().toLowerCase().contains(text.toLowerCase())
                && i.getAvailable())).collect(Collectors.toList());
    }

    @Override
    public boolean existsById(Long itemId) {
        return items.containsKey(itemId);
    }
}