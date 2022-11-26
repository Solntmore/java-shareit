package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

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
    public List<UserDto> findAllUsers() {
        log.debug("Get /users request was received. Get all users.");

        return userService.findAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable long userId) {
        log.debug("Get /users request was received. Get all users.");

        return userService.findUserById(userId);
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid User user) {
        log.debug("A Post/users request was received. Create a user {}.", user);

        return userService.createUser(user);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody User updateUser) {
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
