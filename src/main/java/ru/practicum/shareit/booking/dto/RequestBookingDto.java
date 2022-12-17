package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link ru.practicum.shareit.booking.model.Booking} entity
 */
@Data
public class RequestBookingDto implements Serializable {

    @Future(message = "Start mustn`t be in past")
    @NotNull(message = "Start mustn`t be null")
    private final LocalDateTime start;
    @Future(message = "End mustn`t be in past")
    @NotNull(message = "End mustn`t be null")
    private final LocalDateTime end;

    @NotNull(message = "ItemId mustn`t be null")
    private final Long itemId;

    private Long bookerId;
}