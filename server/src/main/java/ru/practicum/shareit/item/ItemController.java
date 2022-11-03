package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Create;
import ru.practicum.shareit.util.Update;

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
        ItemDto createdItemDto = itemService.create(userId, itemDto);
        log.info("Created {} for owner id={}", createdItemDto, userId);
        return createdItemDto;
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Validated(Update.class) @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemDto updatedItemDto = itemService.update(userId, itemDto, itemId);
        log.info("Updated {} for owner id={}", updatedItemDto, userId);
        return updatedItemDto;
    }

    @GetMapping("/{itemId}")
    public ItemBookingDto find(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        ItemBookingDto foundItem = itemService.getItemDtoById(userId, itemId);
        log.info("Found {} by id={}", foundItem, itemId);
        return foundItem;
    }

    @GetMapping
    public List<ItemBookingDto> getAllItemsDtoOfUser(
            @RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        List<ItemBookingDto> listFoundItemBookingDto = itemService.getAllItemsDtoOfUser(userId, pageRequest);
        log.info("Found for owner id={} {}", userId, listFoundItemBookingDto);
        return listFoundItemBookingDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItemsByNameOrDescription(
            @RequestParam String text,
            @RequestParam(name = "from", defaultValue = "0") Integer from,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size);
        return itemService.searchItemsByNameOrDescription(text, pageRequest);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long id,
                                    @PathVariable Long itemId,
                                    @Validated(Create.class) @RequestBody CommentDto commentDto) {
        return itemService.createComment(id, itemId, commentDto);
    }
}