package ru.practicum.shareit.booking.dto;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RequestBookingDto implements Serializable {

    @Future(message = "Start mustn`t be in past")
    @NotNull(message = "Start mustn`t be null")
    private LocalDateTime start;
    @Future(message = "End mustn`t be in past")
    @NotNull(message = "End mustn`t be null")
    private LocalDateTime end;

    @NotNull(message = "ItemId mustn`t be null")
    private Long itemId;

    private Long bookerId;
}