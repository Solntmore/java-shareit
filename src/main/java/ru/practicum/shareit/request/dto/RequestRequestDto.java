package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.Request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * A DTO for the {@link Request} entity
 */
@Data
public class RequestRequestDto implements Serializable {

    @NotBlank
    @NotNull
    private String description;
}