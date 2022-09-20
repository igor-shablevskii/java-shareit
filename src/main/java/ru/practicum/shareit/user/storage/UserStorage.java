package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user);

    User delete(Long userId);

    User find(Long userId);

    List<User> findAll();

    boolean existsById(Long userId);

    boolean checkEmail(String email);
}