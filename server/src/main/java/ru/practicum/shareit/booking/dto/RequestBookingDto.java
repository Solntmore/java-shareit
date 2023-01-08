package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link Booking} entity
 */
@Data
public class RequestBookingDto implements Serializable {

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;
}