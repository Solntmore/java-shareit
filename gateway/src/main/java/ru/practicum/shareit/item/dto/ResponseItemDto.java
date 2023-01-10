package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.comment.dto.ResponseCommentDto;

import java.io.Serializable;
import java.util.ArrayList;

@Data
public class ResponseItemDto implements Serializable {

    private final Long id;

    private final String name;

    private final String description;

    private final Boolean available;

    private final long requestId;

    private ItemBookingDto lastBooking;

    private ItemBookingDto nextBooking;

    private ArrayList<ResponseCommentDto> comments;
}