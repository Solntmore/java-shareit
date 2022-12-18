package ru.practicum.shareit.booking.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {
    Booking patchStatusOfBooking(long bookingId, boolean approve, long userId);

    Booking findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime now);

    Booking findFirstByItem_IdAndStartIsAfterOrderByStartAsc(long itemId, LocalDateTime now);

    boolean existsBookingByItem_IdAndBooker_IdAndEndIsBefore(long itemId, long userId, LocalDateTime now);

    @Query("SELECT b FROM Booking b WHERE b.booker = (SELECT u FROM User u WHERE u.id = ?1) ORDER BY b.id DESC")
    Page<Booking> findAllByBookerIdOrderByIdAsc(long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.end < ?1 AND b.booker = (SELECT u FROM User u WHERE u.id = ?2) " +
            "ORDER BY b.id DESC")
    Page<Booking> findBookingsInPastByBookerIdOrderByIdAsc(LocalDateTime now, long bookerId, Pageable pageable);

    @Query("SELECT b FROM Booking b WHERE b.start > ?1 AND b.booker = (SELECT u FROM User u WHERE u.id = ?2) " +
            "ORDER BY b.id DESC")
    Page<Booking> findBookingsInFutureByBookerIdOrderByIdAsc(LocalDateTime now, long bookerId, Pageable pageable);

    Page<Booking> findAllByStatusAndBooker_IdOrderByIdDesc(Status status, long bookerId, Pageable pageable);

    Page<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderById(long bookerId, LocalDateTime now,
                                                                           LocalDateTime now2, Pageable pageable);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i WHERE i.owner = ?2 AND b.status = ?1 ORDER BY b.id DESC")
    Page<Booking> findBookingsByStatusAndForOwnerItems(Status status, long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i WHERE i.owner = ?1 ORDER BY b.id DESC")
    Page<Booking> findBookingsForOwnerItems(long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i WHERE i.owner = ?2 AND b.start > ?1 ORDER BY b.id DESC")
    Page<Booking> findBookingsInFutureForOwnerItems(LocalDateTime now, long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i " +
            "WHERE i.owner = ?2 AND b.start < ?1 AND b.end >?1 ORDER BY b.id DESC")
    Page<Booking> findCurrentBookingsForOwnerItems(LocalDateTime now, long ownerId, Pageable pageable);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i WHERE i.owner = ?2 AND b.end < ?1 ORDER BY b.id DESC")
    Page<Booking> findBookingsInPastForOwnerItems(LocalDateTime now, long ownerId, Pageable pageable);
}