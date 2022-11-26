package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class User {

    private long id;

    @NotBlank(message = "Name may not be blank.")
    private String name;

    @Email(message = "Enter the correct email.")
    @NotNull(message = "Email can`t be null.")
    private String email;

}
