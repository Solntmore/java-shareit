package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class RequestRequestDto implements Serializable {

    @NotBlank
    @NotNull
    private String description;
}