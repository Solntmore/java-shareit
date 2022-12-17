package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.shareit.item.model.Item} entity
 */
@Data
public class BookingItemDto implements Serializable {
    private final Long id;
    private final String name;
}