package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

public interface ItemRepositoryCustom {

    Item setOwner(Item item, long ownerId);

    Item patchItem(Item newItem, long itemId, long userId);
}
