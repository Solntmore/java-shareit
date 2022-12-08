package ru.practicum.shareit.comment.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.shareit.comment.model.Comment} entity
 */
@Data
public class RequestCommentDto implements Serializable {
    private final Long id;
    private Long authorId;
}