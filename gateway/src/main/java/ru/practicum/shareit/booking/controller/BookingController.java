package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.RequestState;

import javax.validation.Valid;
import javax.validation.constraints.Min;

import static ru.practicum.shareit.Constants.X_SHADER_USER_ID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                @RequestBody @Valid RequestBookingDto requestBookingDto) {
        log.debug("A Post/bookings request was received. Create a booking {} with owner id {}.", requestBookingDto, userId);

        return bookingClient.createBooking(requestBookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> patchStatusOfBooking(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                       @PathVariable long bookingId,
                                                       @RequestParam boolean approved) {
        log.debug("A Patch/bookings/{}?approved={} request was received. Approve booking with owner id {}.",
                bookingId, approved, userId);

        return bookingClient.patchStatusOfBooking(userId, approved, bookingId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getInfoOfBooking(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                   @PathVariable long bookingId) {
        log.debug("A Get/bookings/{} request was received. Get information of booking {} with owner id {}.",
                bookingId, bookingId, userId);

        return bookingClient.getInfoOfBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsOfUser(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                       @RequestParam(required = false, defaultValue = "ALL")
                                                       RequestState state,
                                                       @RequestParam(required = false, defaultValue = "0")
                                                       @Min(0) int from,
                                                       @RequestParam(required = false, defaultValue = "100")
                                                       @Min(0) int size) {
        log.debug("A Get/bookings?state={} request was received. Get information of  user`s {} bookings with state {}.",
                state.name(), userId, state.name());

        return bookingClient.getAllBookingsOfUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsForUsersItems(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                              @RequestParam(required = false, defaultValue = "ALL")
                                                              RequestState state,
                                                              @RequestParam(required = false, defaultValue = "0")
                                                              @Min(0) int from,
                                                              @RequestParam(required = false, defaultValue = "100")
                                                              @Min(0) int size) {
        log.debug("A Get/bookings?state={} request was received. Get information of  user`s {} bookings with state {}.",
                state.name(), userId, state.name());

        return bookingClient.getAllBookingsForUsersItems(userId, state, from, size);
    }


}
