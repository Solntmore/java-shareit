package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.io.Serializable;

/**
 * A DTO for the {@link Booking} entity
 */
@Data
public class ItemBookingDto implements Serializable {
    private final Long id;
    private final Long bookerId;
}