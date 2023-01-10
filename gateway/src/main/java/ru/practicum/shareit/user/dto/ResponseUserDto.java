package ru.practicum.shareit.user.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.shareit.user.model.User} entity
 */
@Data
public class ResponseUserDto implements Serializable {

    private final Long id;

    private final String name;

    private final String email;
}