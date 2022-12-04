package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

import static ru.practicum.shareit.item.ItemConstants.X_SHADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping(value = {"/search", ""})
    public List<ResponseItemDto> findItemsWithParameters(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                         @RequestParam(name = "text", required = false) String query) {
        log.debug("Method findItemsWithParameters in ItemController is running");

        return itemService.findAllItemsWithParameters(userId, query);
    }

    @GetMapping("/{itemId}")
    public ResponseItemDto findItemById(@PathVariable long itemId) {
        log.debug("Get /items/{} request was received. Get item with itemId {}.", itemId, itemId);

        return itemService.findItemById(itemId);
    }

    @PostMapping
    public ResponseItemDto createItem(@RequestHeader(X_SHADER_USER_ID) long userId,
                                      @RequestBody @Valid RequestItemDto requestItemDto) {
        log.debug("A Post/items request was received. Create an item {} with owner id {}.", requestItemDto, userId);
        userService.findUserById(userId);

        return itemService.createItem(requestItemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseItemDto updateItem(@RequestHeader(X_SHADER_USER_ID) long userId, @RequestBody RequestItemDto newItem,
                                      @PathVariable long itemId) {
        log.debug("A Patch/items request was received. Update an item {} with owner id {}.", newItem, userId);
        userService.findUserById(userId);
        itemService.findItemById(itemId);

        return itemService.updateItem(newItem, itemId, userId);
    }
}
