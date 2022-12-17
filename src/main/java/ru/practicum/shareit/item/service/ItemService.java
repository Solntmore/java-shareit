package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.comment.dto.ResponseCommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.storage.CommentRepository;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.exceptions.NoBookingInPastException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;

    public ResponseItemDto createItem(RequestItemDto requestItemDto, long userId) {

        return itemMapper.itemToResponseItemDto(
                itemRepository.save(
                        itemRepository.setOwner(
                                itemMapper.requestItemDtoToItem(requestItemDto), userId)));
    }

    public ResponseItemDto updateItem(RequestItemDto newItem, long itemId, long userId) {
        if (itemRepository.existsById(itemId) && userRepository.existsById(userId)) {

            return itemMapper.itemToResponseItemDto(
                    itemRepository.patchItem(
                            itemMapper.requestItemDtoToItem(newItem), itemId, userId));
        }
        throw new ItemNotFoundException("User with id " + userId + " or Item with id " + itemId + " not exist");
    }

    public ResponseItemDto findItemAndUserById(long itemId, long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with itemId " + itemId + " is not registered."));

        ResponseItemDto responseItemDto = itemMapper.itemToResponseItemDto(item);
        ArrayList<ResponseCommentDto> comments = commentRepository.findAllByItem_IdOrderById(itemId);
        responseItemDto.setComments(comments);

        if (item.getOwner() != userId) {
            return responseItemDto;
        }

        return setLastAndNextBooking(itemId, responseItemDto);
    }

    public List<ResponseItemDto> findAllItemsWithParameters(long userId, String query) {
        if (query == null) {
            log.debug("Get /items request was received. Get all items with ownerId {}.", userId);

            return itemRepository.findAllByOwnerOrderByIdAsc(userId)
                    .stream()
                    .map(itemMapper::itemToResponseItemDto)
                    .map(responseItemDto -> setLastAndNextBooking(responseItemDto.getId(), responseItemDto))
                    .collect(Collectors.toList());

        } else {
            log.debug("Get /items/text={} request was received. Get all items with query and ownerId {}.", query, userId);
            String lowerQuery = query.toLowerCase();

            if (lowerQuery.isBlank()) {
                return new ArrayList<>();
            }

            return Stream.concat(
                            itemRepository.findAllByDescriptionContainingIgnoreCaseAndAvailableIsTrue(lowerQuery).stream(),
                            itemRepository.findAllByNameContainingIgnoreCaseAndAvailableIsTrue(lowerQuery).stream())
                    .distinct()
                    .sorted(Comparator.comparingLong(Item::getId))
                    .map(itemMapper::itemToResponseItemDto)
                    .map(responseItemDto -> setLastAndNextBooking(responseItemDto.getId(), responseItemDto))
                    .collect(Collectors.toList());
        }
    }

    public ResponseCommentDto addCommentToItem(RequestCommentDto requestCommentDto, long itemId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with itemId " + itemId + " is not registered."));

        if (bookingRepository
                .existsBookingByItem_IdAndBooker_IdAndEndIsBefore(itemId, userId, LocalDateTime.now())) {

            Comment comment = commentMapper.requestCommentDtoToComment(requestCommentDto);
            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());

            return commentMapper.commentToResponseCommentDto(commentRepository.save(comment));
        }
        throw new NoBookingInPastException("You can`t add comment with 0 finished bookings");
    }

    private ResponseItemDto setLastAndNextBooking(long itemId, ResponseItemDto responseItemDto) {
        Optional<Booking> nextBookingDto = Optional.ofNullable(
                bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(itemId, LocalDateTime.now()));
        Optional<Booking> lastBookingDto = Optional.ofNullable(
                bookingRepository.findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(itemId, LocalDateTime.now()));

        if (nextBookingDto.isPresent()) {
            responseItemDto.setNextBooking(
                    bookingMapper.bookingToItemBookingDto(
                            nextBookingDto.get()));
        }
        if (lastBookingDto.isPresent()) {
            responseItemDto.setLastBooking(
                    bookingMapper.bookingToItemBookingDto(
                            lastBookingDto.get()));
        }

        return responseItemDto;
    }
}
