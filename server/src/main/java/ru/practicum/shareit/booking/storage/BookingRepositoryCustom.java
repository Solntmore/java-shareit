package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;


public interface BookingRepositoryCustom {

    Booking patchStatusOfBooking(long bookingId, boolean approve, long userId);
}
