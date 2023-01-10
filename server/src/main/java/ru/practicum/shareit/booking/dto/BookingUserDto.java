package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.io.Serializable;

/**
 * A DTO for the {@link User} entity
 */
@Data
public class BookingUserDto implements Serializable {
    private final Long id;
}