package ru.practicum.shareit.item.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.error.UserNotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.util.ItemMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private long itemId;

    @Autowired
    public ItemServiceImpl(ItemStorage itemStorage, UserStorage userStorage) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.itemId = 0;
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long userId) {
        checkExistsUser(userId);
        Item item = ItemMapper.toItem(itemDto, userId);
        item.setId(++itemId);
        item.setOwnerId(userId);
        itemStorage.create(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        checkExistsItem(itemId);
        checkExistsUser(userId);
        if (!itemStorage.find(itemId).getOwnerId().equals(userId)) {
            throw new UserNotValidException(String.format("Item id = %d not found for user id = %d", itemId, userId));
        }
        Item item = itemStorage.find(itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        itemStorage.update(item);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public Item delete(Long itemId) {
        checkExistsItem(itemId);
        return itemStorage.delete(itemId);
    }

    @Override
    public ItemDto find(Long itemId) {
        checkExistsItem(itemId);
        return ItemMapper.toItemDto(itemStorage.find(itemId));
    }

    @Override
    public List<ItemDto> findAllOwnerItems(Long userId) {
        checkExistsUser(userId);
        List<Item> listItems = new ArrayList<>(itemStorage.findAllOwnerItems(userId));
        return listItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text, Long userId) {
        checkExistsUser(userId);
        List<Item> listItems = new ArrayList<>(itemStorage.search(text));
        return listItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkExistsItem(Long itemId) {
        if (!itemStorage.existsById(itemId)) {
            throw new NotFoundException(String.format("Item with id = %d not found", itemId));
        }
    }

    private void checkExistsUser(Long userId) {
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException(String.format("User with id = %d not found", userId));
        }
    }
}