package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceSpringBootTest {
    @Autowired
    private ItemServiceImpl itemService;
    @Autowired
    private UserServiceImpl userService;
    private ItemDto itemDto;

    @BeforeEach
    void beforeEach() {
        itemDto = new ItemDto(1L, "item1", "description1", true, null);
        UserDto userDto = new UserDto(1L, "user1", "email@mail.ru");
        userService.create(userDto);
    }

    @Test
    void getAllItemsOfUserTest() {
        itemService.create(1L, itemDto);
        List<Item> result = itemService.getAllItemsOfUser(1L, PageRequest.of(0, 2));
        assertThat(result.get(0).getId(), equalTo(1L));
        assertThat(result.get(0).getName(), equalTo(itemDto.getName()));
        assertThat(result.get(0).getDescription(), equalTo(itemDto.getDescription()));
    }
}
