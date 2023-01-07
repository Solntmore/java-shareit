package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.request.model.Request;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * A DTO for the {@link Request} entity
 */
@Data
public class ResponseRequestDto implements Serializable {
    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private List<ResponseItemForRequestDto> items;
}