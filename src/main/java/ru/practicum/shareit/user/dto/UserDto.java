package ru.practicum.shareit.user.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Slf4j
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
