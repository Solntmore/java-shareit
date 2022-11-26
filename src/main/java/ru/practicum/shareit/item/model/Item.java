package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@Data
@AllArgsConstructor
public class Item {

    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    @NotNull
    private Boolean available;
    private long owner;
    private ItemRequest request;
}
