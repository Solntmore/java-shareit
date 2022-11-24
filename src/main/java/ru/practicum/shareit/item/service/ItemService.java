package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.toItemDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemStorage itemStorage;

    public ItemDto createItem(Item item, long userId) {
        return toItemDto(itemStorage.createItem(item, userId));
    }

    public ItemDto updateItem(Item newItem, long itemId, long userId) {
        return itemStorage.updateItem(newItem, itemId, userId);
    }

    public ItemDto findItemById(long itemId) {

        Item item = itemStorage.findItemById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Item with itemId " + itemId + " is not registered."));
        return toItemDto(item);
    }

    public List<ItemDto> findAllItemsWithParameters(long userId, String query) {
        if (query == null) {
            log.debug("Get /items request was received. Get all items with ownerId {}.", userId);

            return itemStorage.findAllItems().stream()
                    .filter(item -> item.getOwner() == userId)
                    .sorted(Comparator.comparingLong(Item::getId))
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            log.debug("Get /items/text={} request was received. Get all items with query and ownerId {}.", query, userId);
            String lowerQuery = query.toLowerCase();

            if (lowerQuery.isBlank()) {
                return new ArrayList<>();
            }

            return itemStorage.findAllItems().stream()
                    .filter(item -> item.getAvailable())
                    .filter(item -> item.getName().toLowerCase().contains(lowerQuery)
                            || item.getDescription().toLowerCase().contains(lowerQuery))
                    .sorted(Comparator.comparingLong(Item::getId))
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
    }
}