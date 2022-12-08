package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.comment.dto.ResponseCommentDto;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A DTO for the {@link ru.practicum.shareit.item.model.Item} entity
 */
@Data
public class ResponseItemDto implements Serializable {

    private final Long id;

    @NotBlank
    private final String name;

    @NotBlank
    private final String description;

    private final Boolean available;

    private ItemBookingDto lastBooking;
    private ItemBookingDto nextBooking;
    private ArrayList<ResponseCommentDto> comments;
}