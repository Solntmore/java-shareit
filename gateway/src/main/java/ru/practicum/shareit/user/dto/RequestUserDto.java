package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.shareit.user.model.User} entity
 */
@Data
public class RequestUserDto implements Serializable {

    @NotBlank(message = "Name may not be blank")
    private String name;

    @Email(message = "Enter the correct email.")
    @NotNull(message = "Email can`t be null.")
    private String email;
}