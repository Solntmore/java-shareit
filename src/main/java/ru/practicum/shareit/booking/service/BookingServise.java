package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.InvalidStatusException;
import ru.practicum.shareit.booking.exception.NotAvailableException;
import ru.practicum.shareit.booking.exception.WrongTimeException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.RequestState;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServise {
    private final ItemRepository itemRepository;

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final UserRepository userRepository;

    public ResponseBookingDto createBooking(RequestBookingDto requestBookingDto, long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));

        long itemId = requestBookingDto.getItemId();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Item with itemId " + itemId + " is not registered."));

        if (item.getOwner() == userId) {
            throw new BookingNotFoundException("Item`s owner and booker can`t be one person");
        }

        if (requestBookingDto.getEnd().isBefore(requestBookingDto.getStart())) {
            throw new WrongTimeException("End time should be after start time");
        }

        if (!item.getAvailable()) {
            throw new NotAvailableException("Item with itemId " + itemId + " is not available.");
        }

        requestBookingDto.setBookerId(userId);
        Booking booking = bookingMapper.requestBookingDtoToBooking(requestBookingDto);
        booking.setItem(item);

        return bookingMapper.bookingToResponseBookingDto(bookingRepository.save(booking));
    }


    public ResponseBookingDto patchStatusOfBooking(long bookingId, boolean approve, long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new BookingNotFoundException("Booking with bookingId " + bookingId + " is not registered."));

        if (booking.getBooker().getId().equals(userId)) {
            throw new BookingNotFoundException("Booker can`t approve booking");
        }

        if (!booking.getStatus().equals(Status.APPROVED)) {
            return bookingMapper.bookingToResponseBookingDto(
                    bookingRepository.patchStatusOfBooking(bookingId, approve, userId));
        }
        throw new InvalidStatusException("Booking is already approved");
    }

    public ResponseBookingDto getInfoOfBooking(long bookingId, long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new BookingNotFoundException("Booking with bookingId " + bookingId + " is not registered."));

        if (booking.getBooker().getId() == userId) {
            return bookingMapper.bookingToResponseBookingDto(booking);
        }

        if (booking.getItem().getOwner() == userId) {
            return bookingMapper.bookingToResponseBookingDto(booking);
        }

        throw new BookingNotFoundException("Only booker or item`s owner allow get booking information.");
    }

    public List<ResponseBookingDto> getAllBookingsOfUser(RequestState state, long userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));
        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return convertToResponseBookingDto(
                        bookingRepository.findAllByBookerIdOrderByIdAsc(userId, pageable));
            case PAST:
                return convertToResponseBookingDto(
                        bookingRepository.findBookingsInPastByBookerIdOrderByIdAsc(now, userId, pageable));
            case FUTURE:
                return convertToResponseBookingDto(
                        bookingRepository.findBookingsInFutureByBookerIdOrderByIdAsc(now, userId, pageable));
            case WAITING:
                return convertToResponseBookingDto(
                        bookingRepository.findAllByStatusAndBooker_IdOrderByIdDesc(Status.WAITING, userId, pageable));
            case REJECTED:
                return convertToResponseBookingDto(
                        bookingRepository.findAllByStatusAndBooker_IdOrderByIdDesc(Status.REJECTED, userId, pageable));
            case CURRENT:
                return convertToResponseBookingDto(
                        bookingRepository.findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderById(userId, now, now,
                                pageable));
            default:
                throw new InvalidStatusException("Unknown state: " + state.name());
        }
    }

    public List<ResponseBookingDto> getAllBookingsForUsersItems(RequestState state, long userId, Pageable pageable) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));

        if (itemRepository.countAllByOwnerOrderById(userId) < 0) {
            throw new ItemNotFoundException("User with id " + userId + "haven`t items");
        }

        LocalDateTime now = LocalDateTime.now();

        switch (state) {
            case ALL:
                return convertToResponseBookingDto(
                        bookingRepository.findBookingsForOwnerItems(userId, pageable));
            case PAST:
                return convertToResponseBookingDto(
                        bookingRepository.findBookingsInPastForOwnerItems(now, userId, pageable));
            case FUTURE:
                return convertToResponseBookingDto(
                        bookingRepository.findBookingsInFutureForOwnerItems(now, userId, pageable));
            case WAITING:
                return convertToResponseBookingDto(
                        bookingRepository.findBookingsByStatusAndForOwnerItems(Status.WAITING, userId, pageable));
            case REJECTED:
                return convertToResponseBookingDto(
                        bookingRepository.findBookingsByStatusAndForOwnerItems(Status.REJECTED, userId, pageable));
            case CURRENT:
                return convertToResponseBookingDto(
                        bookingRepository.findCurrentBookingsForOwnerItems(now, userId, pageable));
            default:
                throw new InvalidStatusException("Unknown state: " + state.name());
        }
    }

    List<ResponseBookingDto> convertToResponseBookingDto(Page<Booking> list) {
        return list.getContent()
                .stream()
                .map(bookingMapper::bookingToResponseBookingDto)
                .collect(Collectors.toList());
    }
}
