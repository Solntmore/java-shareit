package ru.practicum.shareit.comment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Data
public class RequestCommentDto implements Serializable {

    @NotBlank
    private String text;
}