package ru.practicum.shareit.item.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.item.exceptions.ForbiddenOwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.exceptions.InvalidNameException;

import java.util.Optional;

public class ItemRepositoryImpl implements ItemRepositoryCustom {

    private final ItemRepository itemRepository;

    public ItemRepositoryImpl(@Lazy ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    public Item setOwner(Item item, long ownerId) {
        item.setOwner(ownerId);
        return item;
    }

    @Override
    public Item patchItem(Item newItem, long itemId, long userId) {
        Item oldItem = itemRepository.findById(itemId).get();

        if (oldItem.getOwner() != userId) {
            throw new ForbiddenOwnerException("This user with userId " + userId + " is not allowed to change this item.");
        }

        Optional<String> optionalName = Optional.ofNullable(newItem.getName());
        Optional<String> optionalDescription = Optional.ofNullable(newItem.getDescription());
        Optional<Boolean> optionalAvailable = Optional.ofNullable(newItem.getAvailable());

        if (isPresentAndNotBlank(optionalName)) {
            oldItem.setName(optionalName.get());
        }

        if (isPresentAndNotBlank(optionalDescription)) {
            oldItem.setDescription(optionalDescription.get());
        }

        if (optionalAvailable.isPresent()) {
            boolean available = optionalAvailable.get();
            oldItem.setAvailable(available);
        }

        itemRepository.save(oldItem);
        return oldItem;
    }

    private boolean isPresentAndNotBlank(Optional<String> optionalLine) {
        if (optionalLine.isPresent()) {
            String line = optionalLine.get();
            if (line.isBlank()) {
                throw new InvalidNameException("The line cannot be blank");
            }
            return true;
        }
        return false;
    }
}
