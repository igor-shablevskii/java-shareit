package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemRequestStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemRequestStorage itemRequestStorage;
    private User user;
    private User user2;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;
    private ItemRequest itemRequest3;
    private ItemRequest itemRequest4;
    private ItemRequest itemRequest5;
    private ItemRequest itemRequest6;

    @BeforeEach
    void setUp() {
        user = userStorage.save(new User(1L, "user1", "user1@email.com"));
        user2 = userStorage.save(new User(2L, "user2", "user2@email"));
        itemRequest = itemRequestStorage.save(new ItemRequest(1L, "description", user.getId(),
                LocalDateTime.now()));
        itemRequest2 = itemRequestStorage.save(new ItemRequest(2L, "description", user2.getId(),
                LocalDateTime.now().plusMinutes(1)));
        itemRequest3 = itemRequestStorage.save(new ItemRequest(3L, "description", user.getId(),
                LocalDateTime.now().plusMinutes(2)));
        itemRequest4 = itemRequestStorage.save(new ItemRequest(4L, "description", user2.getId(),
                LocalDateTime.now().plusMinutes(3)));
        itemRequest5 = itemRequestStorage.save(new ItemRequest(5L, "description", user.getId(),
                LocalDateTime.now().plusMinutes(4)));
        itemRequest6 = itemRequestStorage.save(new ItemRequest(6L, "description", user2.getId(),
                LocalDateTime.now().plusMinutes(5)));
    }

    @Test
    void findAllByRequester() {

        List<ItemRequest> itemRequests2 = itemRequestStorage.findAllByRequester(user2.getId());
        assertThat(itemRequests2).hasSize(3)
                .contains(itemRequest2)
                .contains(itemRequest4).contains(itemRequest6);
    }

    @Test
    void findItemRequestsFromAnotherUsers() {
        int from = 0;
        int size = 1;
        int page = from / size;
        Page<ItemRequest> itemRequests = itemRequestStorage.findItemRequestsFromAnotherUsers(user.getId(),
                PageRequest.of(page, size));

        assertThat(itemRequests)
                .hasSize(1)
                .contains(itemRequest2);

        from = 2;
        size = 2;
        page = from / size;
        Page<ItemRequest> itemRequests2 = itemRequestStorage.findItemRequestsFromAnotherUsers(user2.getId(),
                PageRequest.of(page, size));

        assertThat(itemRequests2)
                .hasSize(1)
                .contains(itemRequest5);
    }
}