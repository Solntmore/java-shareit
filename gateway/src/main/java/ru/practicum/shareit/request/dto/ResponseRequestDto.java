package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class ResponseRequestDto implements Serializable {
    private final Long id;
    private final String description;
    private final LocalDateTime created;
    private List<ResponseItemForRequestDto> items;
}