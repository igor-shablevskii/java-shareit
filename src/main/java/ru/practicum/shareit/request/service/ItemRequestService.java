package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long id, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getCurrentUserItemRequests(Long id);

    ItemRequestResponseDto getItemRequestById(Long userId, Long requestId);

    List<ItemRequestResponseDto> getItemRequestsFromAnotherUsers(Long id, PageRequest pageRequest);
}