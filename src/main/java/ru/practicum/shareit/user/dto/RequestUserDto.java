package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class RequestUserDto {

    @NotBlank(message = "Name may not be blank")
    private String name;

    @Email(message = "Enter the correct email.")
    @NotNull(message = "Email can`t be null.")
    private String email;
}
