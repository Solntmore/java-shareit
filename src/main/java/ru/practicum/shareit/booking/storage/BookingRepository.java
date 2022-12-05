package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;


public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {
    Booking patchStatusOfBooking(long bookingId, boolean approve, long userId);
}