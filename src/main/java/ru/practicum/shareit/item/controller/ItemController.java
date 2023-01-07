package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.comment.dto.ResponseCommentDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.Convert.toPageRequest;
import static ru.practicum.shareit.item.ItemConstants.X_SHADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Validated
public class ItemController {

    private final ItemService itemService;
    private final UserService userService;

    @GetMapping(value = {"/search", ""})
    public List<ResponseItemDto> findItemsWithParameters(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                         @RequestParam(name = "text", required = false) String query,
                                                         @RequestParam(required = false, defaultValue = "0")
                                                         @Min(0) int from,
                                                         @RequestParam(required = false, defaultValue = "100")
                                                         @Min(0) int size) {
        log.debug("Method findItemsWithParameters in ItemController is running");

        return itemService.findAllItemsWithParameters(userId, query, toPageRequest(from, size));
    }

    @GetMapping("/{itemId}")
    public ResponseItemDto findItemById(@RequestHeader(X_SHADER_USER_ID) long userId, @PathVariable long itemId) {
        log.debug("Get /items/{} request was received. Get item with itemId {}.", itemId, itemId);

        return itemService.findItemAndUserById(itemId, userId);
    }

    @PostMapping
    public ResponseItemDto createItem(@RequestHeader(X_SHADER_USER_ID) long userId,
                                      @RequestBody @Valid RequestItemDto requestItemDto) {
        log.debug("A Post/items request was received. Create an item {} with owner id {}.", requestItemDto, userId);
        userService.findUserById(userId);

        return itemService.createItem(requestItemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addCommentToItem(@RequestHeader(X_SHADER_USER_ID) long userId,
                                               @RequestBody @Valid RequestCommentDto requestCommentDto,
                                               @PathVariable long itemId) {
        log.debug("A Post/items/{}/comment request was received. Post comment on item {} with from user {}.",
                itemId, itemId, userId);

        return itemService.addCommentToItem(requestCommentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseItemDto updateItem(@RequestHeader(X_SHADER_USER_ID) long userId, @RequestBody RequestItemDto newItem,
                                      @PathVariable long itemId) {
        log.debug("A Patch/items request was received. Update an item {} with owner id {}.", newItem, userId);

        return itemService.updateItem(newItem, itemId, userId);
    }
}
