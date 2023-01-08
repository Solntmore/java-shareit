package ru.practicum.shareit.comment.dto;

import lombok.Data;
import ru.practicum.shareit.comment.model.Comment;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link Comment} entity
 */
@Data
public class ResponseCommentDto implements Serializable {

    private final Long id;

    private final String text;

    private final String authorName;

    private final LocalDateTime created;
}