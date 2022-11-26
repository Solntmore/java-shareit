package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@Data
@AllArgsConstructor
public class ItemDto {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
    private Boolean available;
}
