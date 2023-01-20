package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.io.Serializable;

/**
 * A DTO for the {@link Item} entity
 */
@Data
public class ResponseItemForRequestDto implements Serializable {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final long requestId;
}