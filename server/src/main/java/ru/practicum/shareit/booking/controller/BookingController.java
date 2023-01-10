package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constants;
import ru.practicum.shareit.Convert;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.RequestState;
import ru.practicum.shareit.booking.service.BookingServise;

import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingServise bookingServise;

    @PostMapping
    public ResponseBookingDto createBooking(@RequestHeader(Constants.X_SHADER_USER_ID) long userId,
                                            @RequestBody RequestBookingDto requestBookingDto) {
        log.debug("A Post/bookings request was received. Create a booking {} with owner id {}.", requestBookingDto, userId);

        return bookingServise.createBooking(requestBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseBookingDto patchStatusOfBooking(@RequestHeader(Constants.X_SHADER_USER_ID) long userId,
                                                   @PathVariable long bookingId,
                                                   @RequestParam boolean approved) {
        log.debug("A Patch/bookings/{}?approved={} request was received. Approve booking with owner id {}.",
                bookingId, approved, userId);

        return bookingServise.patchStatusOfBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseBookingDto getInfoOfBooking(@RequestHeader(Constants.X_SHADER_USER_ID) long userId,
                                               @PathVariable long bookingId) {
        log.debug("A Get/bookings/{} request was received. Get information of booking {} with owner id {}.",
                bookingId, bookingId, userId);

        return bookingServise.getInfoOfBooking(bookingId, userId);
    }

    @GetMapping
    public List<ResponseBookingDto> getAllBookingsOfUser(@RequestHeader(Constants.X_SHADER_USER_ID) long userId,
                                                         @RequestParam(required = false, defaultValue = "ALL")
                                                         RequestState state,
                                                         @RequestParam (required = false, defaultValue = "0")
                                                             int from,
                                                         @RequestParam(required = false, defaultValue = "100")
                                                             int size) {
        log.debug("A Get/bookings?state={} request was received. Get information of  user`s {} bookings with state {}.",
                state.name(), userId, state.name());

        return bookingServise.getAllBookingsOfUser(state, userId, Convert.toPageRequest(from, size));
    }

    @GetMapping("/owner")
    public List<ResponseBookingDto> getAllBookingsForUsersItems(@RequestHeader(Constants.X_SHADER_USER_ID) long userId,
                                                                @RequestParam(required = false, defaultValue = "ALL")
                                                                RequestState state,
                                                                @RequestParam(required = false, defaultValue = "0")
                                                                    int from,
                                                                @RequestParam(required = false, defaultValue = "100")
                                                                    int size) {
        log.debug("A Get/bookings?state={} request was received. Get information of  user`s {} bookings with state {}.",
                state.name(), userId, state.name());

        return bookingServise.getAllBookingsForUsersItems(state, userId, Convert.toPageRequest(from, size));
    }


}
