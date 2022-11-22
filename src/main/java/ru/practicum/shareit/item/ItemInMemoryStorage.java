package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.Counter;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exceptions.ForbiddenOwnerException;
import ru.practicum.shareit.item.exceptions.InvalidDescriptionException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceptions.InvalidNameException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import static ru.practicum.shareit.item.ItemMapper.toItemDto;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ItemInMemoryStorage implements ItemStorage {

    private final HashMap<Long, Item> itemsMap = new HashMap<>();
    private final Counter counter = new Counter();

    @Override
    public Item createItem(Item item, long userId) {
        item.setId(counter.increaseId());
        item.setOwner(userId);
        itemsMap.put(item.getId(), item);
        return item;
    }

    @Override
    public ItemDto updateItem(Item newItem, long itemId, long userId) {
        Item oldItem = itemsMap.get(itemId);

        if (oldItem.getOwner() != userId) {
            throw new ForbiddenOwnerException("This user with userId " + userId + " is not allowed to change this item.");
        }

        Optional<String> optionalName = Optional.ofNullable(newItem.getName());
        Optional<String> optionalDescription = Optional.ofNullable(newItem.getDescription());
        Optional<Boolean> optionalAvailable = Optional.ofNullable(newItem.getAvailable());

        if (optionalName.isPresent()) {
            String name = optionalName.get();
            if (name.isBlank()) {
                throw new InvalidNameException("The name cannot be blank");
            }
            oldItem.setName(name);
        }

        if (optionalDescription.isPresent()) {
            String description = optionalDescription.get();
            if (description.isBlank()) {
                throw new InvalidDescriptionException("The description cannot be blank.");
            }
            oldItem.setDescription(description);
        }

        if (optionalAvailable.isPresent()) {
            boolean available = optionalAvailable.get();
            oldItem.setAvailable(available);
        }

        itemsMap.put(oldItem.getId(), oldItem);
        return toItemDto(oldItem);
    }

    @Override
    public Optional<Item> findItemById(long itemId) {
        return Optional.ofNullable(itemsMap.get(itemId));
    }

    @Override
    public Collection<Item> findAllItems() {
        return itemsMap.values();
    }
}
