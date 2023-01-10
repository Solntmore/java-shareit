package ru.practicum.shareit.user.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestUserDto implements Serializable {

    private String name;

    private String email;
}