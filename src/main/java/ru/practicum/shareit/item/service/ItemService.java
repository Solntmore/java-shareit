package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemStorage itemStorage;

    private final ItemRepository itemRepository;
    private final ItemMapperImpl itemMapper;

    public ResponseItemDto createItem(RequestItemDto requestItemDto, long userId) {

        return itemMapper.itemToDto(
                itemRepository.save(
                        itemRepository.setOwner(
                                itemMapper.requestItemDtoToItem(requestItemDto), userId)));
    }

    public ResponseItemDto updateItem(RequestItemDto newItem, long itemId, long userId) {

        return itemMapper.itemToDto(
                itemRepository.patchItem(
                        itemMapper.requestItemDtoToItem(newItem), itemId, userId));
    }

    public ResponseItemDto findItemById(long itemId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with itemId " + itemId + " is not registered."));
        return itemMapper.itemToDto(item);
    }

    public List<ResponseItemDto> findAllItemsWithParameters(long userId, String query) {
        if (query == null) {
            log.debug("Get /items request was received. Get all items with ownerId {}.", userId);

            /* Попробовать с сортировкой findAllSort */
            return itemRepository.findAll()
                    .stream()
                    .filter(item -> item.getOwner() == userId)
                    .sorted(Comparator.comparingLong(Item::getId))
                    .map(itemMapper::itemToDto)
                    .collect(Collectors.toList());
        } else {
            log.debug("Get /items/text={} request was received. Get all items with query and ownerId {}.", query, userId);
            String lowerQuery = query.toLowerCase();

            if (lowerQuery.isBlank()) {
                return new ArrayList<>();
            }

            return itemRepository.findAll()
                    .stream()
                    .filter(item -> item.getAvailable())
                    .filter(item -> item.getName().toLowerCase().contains(lowerQuery)
                            || item.getDescription().toLowerCase().contains(lowerQuery))
                    .sorted(Comparator.comparingLong(Item::getId))
                    .map(itemMapper::itemToDto)
                    .collect(Collectors.toList());
        }
    }
}
