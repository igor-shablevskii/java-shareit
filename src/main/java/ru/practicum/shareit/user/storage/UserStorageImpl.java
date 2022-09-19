package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> users;
    private long userId;

    public UserStorageImpl() {
        this.users = new HashMap<>();
        this.userId = 0;
    }

    @Override
    public User create(User user) {
        user.setId(++userId);
        users.put(userId, user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User delete(Long userId) {
        return users.remove(userId);
    }

    @Override
    public User find(Long userId) {
        return users.get(userId);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean existsById(Long userId) {
        return users.containsKey(userId);
    }

    @Override
    public boolean checkEmail(String email) {
        return users.values()
                .stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }
}