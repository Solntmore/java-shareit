package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {
    Booking patchStatusOfBooking(long bookingId, boolean approve, long userId);

    Booking findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(long itemId, LocalDateTime localDateTime);

    Booking findFirstByItem_IdAndStartIsAfterOrderByStartAsc(long itemId, LocalDateTime localDateTime);

    boolean existsBookingByItem_IdAndBooker_IdAndEndIsBefore(long itemId, long userId, LocalDateTime localDateTime);

    @Query("SELECT b FROM Booking b WHERE b.booker = (SELECT u FROM User u WHERE u.id = ?1) ORDER BY b.id DESC")
    List<Booking> findAllByBookerIdOrderByIdAsc(long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.end < ?1 AND b.booker = (SELECT u FROM User u WHERE u.id = ?2) " +
            "ORDER BY b.id DESC")
    List<Booking> findBookingsInPastByBookerIdOrderByIdAsc(LocalDateTime localDateTime, long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.start > ?1 AND b.booker = (SELECT u FROM User u WHERE u.id = ?2) " +
            "ORDER BY b.id DESC")
    List<Booking> findBookingsInFutureByBookerIdOrderByIdAsc(LocalDateTime localDateTime, long bookerId);

    List<Booking> findAllByStatusAndBooker_IdOrderByIdDesc(Status status, long bookerId);

    List<Booking> findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderById(long bookerId, LocalDateTime localDateTime,
                                                                           LocalDateTime localDateTime2);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i WHERE i.owner = ?2 AND b.status = ?1 ORDER BY b.id DESC")
    List<Booking> findBookingsByStatusAndForOwnerItems(Status status, long ownerId);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i WHERE i.owner = ?1 ORDER BY b.id DESC")
    List<Booking> findBookingsForOwnerItems(long ownerId);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i WHERE i.owner = ?2 AND b.start > ?1 ORDER BY b.id DESC")
    List<Booking> findBookingsInFutureForOwnerItems(LocalDateTime localDateTime, long ownerId);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i " +
            "WHERE i.owner = ?2 AND b.start < ?1 AND b.end >?1 ORDER BY b.id DESC")
    List<Booking> findCurrentBookingsForOwnerItems(LocalDateTime localDateTime, long ownerId);

    @Query("SELECT b FROM Booking b LEFT JOIN Item i ON b.item = i WHERE i.owner = ?2 AND b.end < ?1 ORDER BY b.id DESC")
    List<Booking> findBookingsInPastForOwnerItems(LocalDateTime localDateTime, long ownerId);
}