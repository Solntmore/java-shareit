package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, ItemRepositoryCustom {

    Item setOwner(Item item, long ownerId);

    Item patchItem(Item newItem, long itemId, long ownerId);

    List<Item> findAllByDescriptionContainingIgnoreCaseAndAvailableIsTrue(String text);

    List<Item> findAllByNameContainingIgnoreCaseAndAvailableIsTrue(String text);

    Page<Item> findAllByOwnerOrderByIdAsc(long id, Pageable pageable);

    long countAllByOwnerOrderById(long ownerId);

    boolean existsById(long id);

    List<Item> findAllByRequestId(long requestId);
}
