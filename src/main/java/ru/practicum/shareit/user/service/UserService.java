package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;
    private final UserMapperImpl userMapper;

    public UserDto createUser(User user) {
        return userMapper.userToDto(userStorage.createUser(user));
    }

    public UserDto findUserById(long userId) {
        User user = userStorage.findUserById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));
        return userMapper.userToDto(user);
    }

    public UserDto updateUser(long userId, User updateUser) {
        return userMapper.userToDto(userStorage.updateUser(userId, updateUser));
    }

    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }

    public List<UserDto> findAllUsers() {
        return userStorage.findAllUsers()
                .stream().sorted(Comparator.comparingLong(User::getId))
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }
}
