package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.min;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.RequestState;
import ru.practicum.shareit.booking.service.BookingServise;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import static ru.practicum.shareit.Convert.toPageRequest;
import static ru.practicum.shareit.item.ItemConstants.X_SHADER_USER_ID;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingServise bookingServise;

    @PostMapping
    public ResponseBookingDto createBooking(@RequestHeader(X_SHADER_USER_ID) long userId,
                                            @RequestBody @Valid RequestBookingDto requestBookingDto) {
        log.debug("A Post/bookings request was received. Create a booking {} with owner id {}.", requestBookingDto, userId);

        return bookingServise.createBooking(requestBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto patchStatusOfBooking(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                   @PathVariable long bookingId,
                                                   @RequestParam boolean approved) {
        log.debug("A Patch/bookings/{}?approved={} request was received. Approve booking with owner id {}.",
                bookingId, approved, userId);

        return bookingServise.patchStatusOfBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getInfoOfBooking(@RequestHeader(X_SHADER_USER_ID) long userId,
                                               @PathVariable long bookingId) {
        log.debug("A Get/bookings/{} request was received. Get information of booking {} with owner id {}.",
                bookingId, bookingId, userId);

        return bookingServise.getInfoOfBooking(bookingId, userId);
    }

    @GetMapping
    public List<ResponseBookingDto> getAllBookingsOfUser(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                         @RequestParam(required = false, defaultValue = "ALL")
                                                         RequestState state,
                                                         @RequestParam (required = false, defaultValue = "0")
                                                             @Min(0) int from,
                                                         @RequestParam(required = false, defaultValue = "100")
                                                             @Min(0) int size) {
        log.debug("A Get/bookings?state={} request was received. Get information of  user`s {} bookings with state {}.",
                state.name(), userId, state.name());

        return bookingServise.getAllBookingsOfUser(state, userId, toPageRequest(from, size));
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getAllBookingsForUsersItems(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                                @RequestParam(required = false, defaultValue = "ALL")
                                                                RequestState state,
                                                                @RequestParam(required = false, defaultValue = "0")
                                                                    @Min(0) int from,
                                                                @RequestParam(required = false, defaultValue = "100")
                                                                    @Min(0) int size) {
        log.debug("A Get/bookings?state={} request was received. Get information of  user`s {} bookings with state {}.",
                state.name(), userId, state.name());

        return bookingServise.getAllBookingsForUsersItems(state, userId, toPageRequest(from, size));
    }


}
