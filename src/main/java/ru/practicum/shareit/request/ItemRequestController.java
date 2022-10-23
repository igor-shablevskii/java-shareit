package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") Long id,
                                            @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(id, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getCurrentUserItemRequests(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemRequestService.getCurrentUserItemRequests(id);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getItemRequestsFromAnotherUsers(
            @RequestHeader("X-Sharer-User-Id") Long id,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
            @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        int page = from / size;
        final PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));
        return itemRequestService.getItemRequestsFromAnotherUsers(id, pageRequest);
    }

    @GetMapping("/{requestId}")
    public ItemRequestResponseDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") Long id,
                                                     @PathVariable Long requestId) {
        return itemRequestService.getItemRequestById(id, requestId);
    }
}
