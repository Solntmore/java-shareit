package ru.practicum.shareit.comment.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ResponseCommentDto implements Serializable {

    private final Long id;

    private final String text;

    private final String authorName;

    private final LocalDateTime created;
}