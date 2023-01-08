package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ItemBookingDto implements Serializable {
    private final Long id;
    private final Long bookerId;
}