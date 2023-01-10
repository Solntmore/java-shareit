package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link Booking} entity
 */
@Data
public class ResponseBookingDto implements Serializable {
    private final Long id;
    private final LocalDateTime start;
    private final LocalDateTime end;
    private final BookingItemDto item;
    private final BookingUserDto booker;
    private final Status status;
}