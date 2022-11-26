package ru.practicum.shareit.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
public class UserDto {

    @Positive
    private long id;

    @NotBlank(message = "Name may not be blank")
    private String name;

    @Email
    private String email;
}
