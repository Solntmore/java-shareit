package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item createItem(Item item, long userId);

    ItemDto updateItem(Item newItem, long itemId, long userId);

    Optional<Item> findItemById(long itemId);

    Collection<Item> findAllItems();
}
