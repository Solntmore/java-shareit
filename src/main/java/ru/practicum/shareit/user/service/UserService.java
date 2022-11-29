package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
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

    /* Выяснил, что Dto нужна не только на выход, но и на вход. Преобразование и из Dto в User,
    и из User в Dto правильно делать в сервисе? Кажется, как будто получается нагромождение */
    public ResponseUserDto createUser(RequestUserDto requestUserDto) {

        return userMapper.userToDto(
                userStorage.createUser(
                        userMapper.requestDtoToUser(requestUserDto)));
    }

    public ResponseUserDto findUserById(long userId) {
        User user = userStorage.findUserById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));
        return userMapper.userToDto(user);
    }

    public ResponseUserDto updateUser(long userId, RequestUserDto updateUser) {
        return userMapper.userToDto(
                userStorage.updateUser(userId, userMapper.requestDtoToUser(updateUser)));
    }

    public void deleteUserById(long id) {
        userStorage.deleteUserById(id);
    }

    public List<ResponseUserDto> findAllUsers() {
        return userStorage.findAllUsers()
                .stream().sorted(Comparator.comparingLong(User::getId))
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }
}
