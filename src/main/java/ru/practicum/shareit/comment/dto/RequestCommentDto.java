package ru.practicum.shareit.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * A DTO for the {@link ru.practicum.shareit.comment.model.Comment} entity
 */
@Data
public class RequestCommentDto implements Serializable {

    @NotBlank
    String text;
}