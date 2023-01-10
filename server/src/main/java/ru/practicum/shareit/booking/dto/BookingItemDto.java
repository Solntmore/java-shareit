package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import java.io.Serializable;

/**
 * A DTO for the {@link Item} entity
 */
@Data
public class BookingItemDto implements Serializable {
    private final Long id;
    private final String name;
}