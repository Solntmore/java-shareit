package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.Convert;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.comment.dto.ResponseCommentDto;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping(value = {"/search", ""})
    public List<ResponseItemDto> findItemsWithParameters(@RequestHeader(Constants.X_SHADER_USER_ID) long userId,
                                                         @RequestParam(name = "text", required = false) String query,
                                                         @RequestParam(required = false, defaultValue = "0") int from,
                                                         @RequestParam(required = false, defaultValue = "100")
                                                             int size) {
        log.debug("Method findItemsWithParameters in ItemController is running");

        return itemService.findAllItemsWithParameters(userId, query, Convert.toPageRequest(from, size));
    }

    @GetMapping("/{itemId}")
    public ResponseItemDto findItemById(@RequestHeader(Constants.X_SHADER_USER_ID) long userId, @PathVariable long itemId) {
        log.debug("Get /items/{} request was received. Get item with itemId {}.", itemId, itemId);

        return itemService.findItemAndUserById(itemId, userId);
    }

    @PostMapping
    public ResponseItemDto createItem(@RequestHeader(Constants.X_SHADER_USER_ID) long userId,
                                      @RequestBody RequestItemDto requestItemDto) {
        log.debug("A Post/items request was received. Create an item {} with owner id {}.", requestItemDto, userId);

        return itemService.createItem(requestItemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseCommentDto addCommentToItem(@RequestHeader(Constants.X_SHADER_USER_ID) long userId,
                                               @RequestBody RequestCommentDto requestCommentDto,
                                               @PathVariable long itemId) {
        log.debug("A Post/items/{}/comment request was received. Post comment on item {} with from user {}.",
                itemId, itemId, userId);

        return itemService.addCommentToItem(requestCommentDto, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseItemDto updateItem(@RequestHeader(Constants.X_SHADER_USER_ID) long userId, @RequestBody RequestItemDto newItem,
                                      @PathVariable long itemId) {
        log.debug("A Patch/items request was received. Update an item {} with owner id {}.", newItem, userId);

        return itemService.updateItem(newItem, itemId, userId);
    }
}
