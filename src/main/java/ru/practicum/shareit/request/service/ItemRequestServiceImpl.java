package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemRequestDto createItemRequest(Long id, ItemRequestDto itemRequestDto) {
        userService.find(id);
        ItemRequest itemRequest = repository.save(ItemRequestMapper.toItemRequest(itemRequestDto, id));
        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Override
    public List<ItemRequestResponseDto> getCurrentUserItemRequests(Long id) {
        userService.find(id);
        List<ItemRequestResponseDto> result = new ArrayList<>();
        List<ItemRequest> itemRequests = repository.findAllByRequester(id);
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> itemDtoList = ItemMapper.toListItemDto(itemRepository.findAllItemsByRequest(itemRequest.getId()));
            result.add(ItemRequestMapper.toItemRequestResponseDto(itemRequest, itemDtoList));
        }
        return result;
    }

    @Override
    public ItemRequest getItemRequestById(Long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Request not found!!"));
    }

    @Override
    public List<ItemRequestResponseDto> getItemRequestsFromAnotherUsers(Long id, PageRequest pageRequest) {
        userService.find(id);
        List<ItemRequestResponseDto> result = new ArrayList<>();
        Page<ItemRequest> itemRequests = repository.findItemRequestsFromAnotherUsers(id,
                pageRequest);
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDto> itemDtoList = ItemMapper.toListItemDto(itemRepository.findAllItemsByRequest(itemRequest.getId()));
            result.add(ItemRequestMapper.toItemRequestResponseDto(itemRequest, itemDtoList));
        }
        return result;
    }

    @Override
    public ItemRequestResponseDto getItemRequestById(Long userId, Long requestId) {
        userService.find(userId);
        List<ItemDto> itemDtoList = ItemMapper.toListItemDto(itemRepository.findAllItemsByRequest(requestId));
        return ItemRequestMapper.toItemRequestResponseDto(getItemRequestById(requestId), itemDtoList);
    }
}
