package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

public interface UserRepositoryCustom {
    User patchUser(long userId, User updateUser);
}
