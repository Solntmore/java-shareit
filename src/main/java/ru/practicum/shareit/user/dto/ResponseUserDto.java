package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.shareit.user.model.User} entity
 */
@Data
public class ResponseUserDto implements Serializable {

    @Positive
    private final Long id;

    @NotBlank(message = "Name may not be blank")
    private final String name;

    @Email(message = "Enter the correct email.")
    @NotNull(message = "Email can`t be null.")
    private final String email;
}