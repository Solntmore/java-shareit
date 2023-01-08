package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseItemForRequestDto implements Serializable {
    private final Long id;
    private final String name;
    private final String description;
    private final Boolean available;
    private final long requestId;
}