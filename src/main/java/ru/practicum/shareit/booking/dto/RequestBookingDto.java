package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link ru.practicum.shareit.booking.model.Booking} entity
 */
@Data
public class RequestBookingDto implements Serializable {
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Long itemId;
    private Long bookerId;
}