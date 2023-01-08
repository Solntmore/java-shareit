package ru.practicum.shareit.comment.dto;

import lombok.Data;
import ru.practicum.shareit.comment.model.Comment;

import java.io.Serializable;

/**
 * A DTO for the {@link Comment} entity
 */
@Data
public class RequestCommentDto implements Serializable {

    String text;
}