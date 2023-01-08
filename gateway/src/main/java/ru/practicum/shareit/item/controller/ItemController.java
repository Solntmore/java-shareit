package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.RequestItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.Constants.X_SHADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping(value = {"/search", ""})
    public ResponseEntity<Object> findItemsWithParameters(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                          @RequestParam(name = "text", required = false) String query,
                                                          @RequestParam(required = false, defaultValue = "0")
                                                         @Min(0) int from,
                                                          @RequestParam(required = false, defaultValue = "100")
                                                         @Min(0) int size) {
        log.debug("Method findItemsWithParameters in ItemController is running");

        return itemClient.findItemsWithParameters(userId, query, from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findItemById(@RequestHeader(X_SHADER_USER_ID) long userId, @PathVariable long itemId) {
        log.debug("Get /items/{} request was received. Get item with itemId {}.", itemId, itemId);

        return itemClient.findItemById(userId, itemId);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(X_SHADER_USER_ID) long userId,
                                      @RequestBody @Valid RequestItemDto requestItemDto) {
        log.debug("A Post/items request was received. Create an item {} with owner id {}.", requestItemDto, userId);

        return itemClient.createItem(requestItemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addCommentToItem(@RequestHeader(X_SHADER_USER_ID) long userId,
                                               @RequestBody @Valid RequestCommentDto requestCommentDto,
                                               @PathVariable long itemId) {
        log.debug("A Post/items/{}/comment request was received. Post comment on item {} with from user {}.",
                itemId, itemId, userId);

        return itemClient.addCommentToItem(requestCommentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(X_SHADER_USER_ID) long userId, @RequestBody RequestItemDto newItem,
                                      @PathVariable long itemId) {
        log.debug("A Patch/items request was received. Update an item {} with owner id {}.", newItem, userId);

        return itemClient.updateItem(newItem, userId, itemId);
    }
}
