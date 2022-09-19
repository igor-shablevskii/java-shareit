package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Create;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto create(@RequestBody @Validated(Create.class) ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto createdItemDto = itemService.create(itemDto, userId);
        log.info("Created {} for owner id={}", createdItemDto, userId);
        return createdItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto updatedItemDto = itemService.update(itemDto, itemId, userId);
        log.info("Updated {} for owner id={}", updatedItemDto, userId);
        return updatedItemDto;
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable Long itemId) {
        ItemDto removedItem = itemService.delete(itemId);
        log.info("Delete {}", removedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto find(@PathVariable Long itemId) {
        ItemDto foundItem = itemService.find(itemId);
        log.info("Found {} by id={}", foundItem, itemId);
        return foundItem;
    }

    @GetMapping
    public List<ItemDto> findAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> listFoundItemsDto = itemService.findAllOwnerItems(userId);
        log.info("Found for owner id={} {}", userId, listFoundItemsDto);
        return listFoundItemsDto;
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text, @RequestHeader("X-Sharer-User-Id") Long userId) {
        List<ItemDto> listFoundItemsDto = itemService.search(text, userId);
        log.info("Found by text '{}' for owner id={} {}", text, userId, listFoundItemsDto);
        return listFoundItemsDto;
    }
}