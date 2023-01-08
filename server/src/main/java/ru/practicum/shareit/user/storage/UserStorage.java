package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User createUser(User user);

    Optional<User> findUserById(long userId);

    User updateUser(long userId, User updateUser);

    void deleteUserById(long userId);

    Collection<User> findAllUsers();
}
