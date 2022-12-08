package ru.practicum.shareit.booking.storage;

import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.exception.ForbiddenBookerException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class BookingRepositoryImpl implements BookingRepositoryCustom {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public BookingRepositoryImpl(@Lazy BookingRepository bookingRepository, @Lazy UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Booking patchStatusOfBooking(long bookingId, boolean approve, long userId) {
        Booking oldBooking = bookingRepository.findById(bookingId).get();


        if (oldBooking.getItem().getOwner() == userRepository.findById(userId).get().getId()) {
            if (approve) {
                oldBooking.setStatus(Status.APPROVED);
            } else {
                oldBooking.setStatus(Status.REJECTED);
            }
            return bookingRepository.save(oldBooking);
        }

        throw new ForbiddenBookerException("This user with userId " + userId + " is not allowed to approve " +
                "this booking.");
    }

}
