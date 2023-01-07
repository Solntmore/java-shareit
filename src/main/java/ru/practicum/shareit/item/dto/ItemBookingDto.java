package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.shareit.booking.model.Booking} entity
 */
@Data
public class ItemBookingDto implements Serializable {
    private final Long id;
    private final Long bookerId;
}