package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.RequestUserDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.debug("Get /users request was received. Get all users.");

        return userClient.findAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findUserById(@PathVariable long userId) {
        log.debug("Get /users request was received. Get all users.");

        return userClient.findUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Valid RequestUserDto requestUserDto) {
        log.debug("A Post/users request was received. Create a user {}.", requestUserDto);

        return userClient.createUser(requestUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId, @RequestBody RequestUserDto updateUser) {
        log.debug("Patch /users request was received. Update user {}.", userId);

        return userClient.updateUser(userId, updateUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        log.debug("The Delete /users/{} request was received. Delete a user by user Id {}.", userId, userId);

        return userClient.deleteUserById(userId);
    }


}
