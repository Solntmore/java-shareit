package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
public class ResponseItemDto {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
    private Boolean available;
}
