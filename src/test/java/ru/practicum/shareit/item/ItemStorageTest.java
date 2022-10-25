package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private ItemRequestStorage itemRequestStorage;
    private User owner;
    private User owner2;
    private ItemRequest itemRequest;
    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;

    @BeforeEach
    void setUp() {
        owner = userStorage.save(new User(
                1L,
                "user",
                "user@email.com"));
        owner2 = userStorage.save(new User(
                2L,
                "user2",
                "user2@email.com"));
        itemRequest = itemRequestStorage.save(new ItemRequest(
                1L,
                "description",
                owner2.getId(),
                LocalDateTime.now().withNano(0)));
        item1 = itemStorage.save(new Item(
                1L,
                "name",
                "description1",
                true,
                owner,
                itemRequest));
        item2 = itemStorage.save(new Item(
                2L,
                "name",
                "description2",
                true,
                owner,
                itemRequest));
        item3 = itemStorage.save(new Item(
                3L,
                "name",
                "description3",
                true,
                owner2,
                null));
        item4 = itemStorage.save(new Item(
                4L,
                "name",
                "description4",
                true,
                owner2,
                null));
    }

    @Test
    void findByOwnerId() {
        int from = 1;
        int size = 1;
        int page = from / size;
        List<Item> items1 = itemStorage.findByOwnerId(owner.getId(), PageRequest.of(page, size));

        assertThat(items1).hasSize(1)
                .contains(item2);

        List<Item> items2 = itemStorage.findByOwnerId(owner2.getId(), PageRequest.of(page, size));

        assertThat(items2).hasSize(1)
                .contains(item4);
    }

    @Test
    void findByIdAndOwnerId() {
        Optional<Item> emptyItem = itemStorage.findByIdAndOwnerId(item1.getId(), owner2.getId());
        Optional<Item> emptyItem2 = itemStorage.findByIdAndOwnerId(item4.getId(), owner.getId());

        assertThat(emptyItem).isEmpty();
        assertThat(emptyItem2).isEmpty();

        Optional<Item> item = itemStorage.findByIdAndOwnerId(item1.getId(), owner.getId());
        Optional<Item> item2 = itemStorage.findByIdAndOwnerId(item3.getId(), owner2.getId());

        assertThat(item).contains(item1);
        assertThat(item2).contains(item3);
    }

    @Test
    void deleteItemByIdAndOwnerId() {
        itemStorage.deleteItemByIdAndOwnerId(item1.getId(), owner.getId());
        assertThat(itemStorage.existsById(item1.getId())).isFalse();

        itemStorage.deleteItemByIdAndOwnerId(item2.getId(), owner2.getId());
        assertThat(itemStorage.existsById(item2.getId())).isTrue();
    }

    @Test
    void searchItemsByNameOrDescription() {
        int from = 2;
        int size = 2;
        int page = from / size;
        List<Item> items = itemStorage.searchItemsByNameOrDescription("NaM", PageRequest.of(page, size));

        assertThat(items).hasSize(2).contains(item3).contains(item4);

        from = 1;
        size = 1;
        page = from / size;
        List<Item> items2 = itemStorage.searchItemsByNameOrDescription("TiOn", PageRequest.of(page, size));
        assertThat(items2).hasSize(1).contains(item2);
    }

    @Test
    void findAllItemsByRequest() {
        List<Item> items = itemStorage.findAllItemsByRequest(itemRequest.getId());

        assertThat(items).hasSize(2).contains(item1).contains(item2);
    }
}
