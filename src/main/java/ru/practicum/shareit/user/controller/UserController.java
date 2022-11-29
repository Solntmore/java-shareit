package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<ResponseUserDto> findAllUsers() {
        log.debug("Get /users request was received. Get all users.");

        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseUserDto findUserById(@PathVariable long userId) {
        log.debug("Get /users request was received. Get all users.");

        return userService.findUserById(userId);
    }

    @PostMapping
    public ResponseUserDto createUser(@RequestBody @Valid RequestUserDto requestUserDto) {
        log.debug("A Post/users request was received. Create a user {}.", requestUserDto);

        return userService.createUser(requestUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseUserDto updateUser(@PathVariable long userId, @RequestBody RequestUserDto updateUser) {
        log.debug("Patch /users request was received. Update user {}.", userId);
        userService.findUserById(userId);

        return userService.updateUser(userId, updateUser);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.debug("The Delete /users/{} request was received. Delete a user by user Id {}.", userId, userId);
        userService.findUserById(userId);
        userService.deleteUserById(userId);
    }


}
