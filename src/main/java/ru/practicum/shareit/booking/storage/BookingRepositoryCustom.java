package ru.practicum.shareit.booking.storage;

import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Set;

public interface BookingRepositoryCustom {

    Booking patchStatusOfBooking(long bookingId, boolean approve, long userId);

}
