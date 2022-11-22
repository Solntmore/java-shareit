package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    /*
     Прошу подойти к работе максимально придирчиво(в рамках разумного), у меня в четверг выходной,
     как раз к этому времени работа будет проверена и я готов выделить время на улучшение кода, чтобы далее не допускать
     систематических ошибок, если они есть, как по код-стайлу, так и по ответственности слоев и др.
     */
    private final ItemService itemService;
    private final UserService userService;


    @GetMapping(value = {"/search", ""})
    public List<ItemDto> findItemsWithParameters(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @RequestParam(name = "text", required = false) String query) {
        if (query == null) {
            log.debug("Get /items request was received. Get all items with ownerId {}.", userId);

            return itemService.findAllItems(userId);
        } else {
            log.debug("Get /items/text={} request was received. Get all items with query and ownerId {}.", query, userId);

            return itemService.findItemByDescriptionAndOwnerId(query);
        }
    }

    @GetMapping("/{itemId}")
    public ItemDto findItemById(@PathVariable long itemId) {
        log.debug("Get /items/{} request was received. Get item with itemId {}.", itemId, itemId);

        return itemService.findItemById(itemId);
    }

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody @Valid Item item) {
        log.debug("A Post/items request was received. Create an item {} with owner id {}.", item, userId);
        userService.findUserById(userId);

        return itemService.createItem(item, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody Item newItem,
                              @PathVariable long itemId) {
        log.debug("A Patch/items request was received. Update an item {} with owner id {}.", newItem, userId);
        userService.findUserById(userId);
        itemService.findItemById(itemId);

        return itemService.updateItem(newItem, itemId, userId);
    }
}
