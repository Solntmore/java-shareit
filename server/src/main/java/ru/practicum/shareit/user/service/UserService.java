package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public ResponseUserDto createUser(RequestUserDto requestUserDto) {

        return userMapper.userToResponseUserDto(
                userRepository.save(
                        userMapper.requestDtoToUser(requestUserDto)));
    }

    public ResponseUserDto findUserById(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));

        return userMapper.userToResponseUserDto(user);
    }

    public ResponseUserDto updateUser(long userId, RequestUserDto updateUser) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));

        return userMapper.userToResponseUserDto(
                userRepository.patchUser(userId, userMapper.requestDtoToUser(updateUser)));
    }

    public void deleteUserById(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));

        userRepository.deleteById(userId);
    }

    public List<ResponseUserDto> findAllUsers() {

        return userRepository.findAll()
                .stream().sorted(Comparator.comparingLong(User::getId))
                .map(userMapper::userToResponseUserDto)
                .collect(Collectors.toList());
    }
}
