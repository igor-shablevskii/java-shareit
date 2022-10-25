package ru.practicum.shareit.request;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemRequestMapper {
    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        return new ItemRequest(itemRequestDto.getId(),
                itemRequestDto.getDescription(),
                requesterId,
                itemRequestDto.getCreated());
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return new ItemRequestDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated());
    }

    public static ItemRequestResponseDto toItemRequestResponseDto(ItemRequest itemRequest, List<Item> itemList) {
        return new ItemRequestResponseDto(itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                new ArrayList<>(itemList.stream()
                        .map(ItemRequestMapper::toItemForRequest)
                        .collect(Collectors.toList())));
    }

    private static ItemRequestResponseDto.Item toItemForRequest(Item item) {
        return new ItemRequestResponseDto.Item(item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest().getId());
    }
}