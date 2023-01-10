package ru.practicum.shareit.item.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class RequestItemDto implements Serializable {

    private String name;

    private String description;

    private Boolean available;

    private long requestId;
}