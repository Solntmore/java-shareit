package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.*;

/*
Сделал тест только на этот репозиторий, так как только в нем есть кастомные запросы с аннотацией @Query
"Есть смысл написать тесты для тех репозиториев, которые содержат кастомные запросы. Кастомные запросы — запросы,
написанные вами с помощью аннотации @Query" гласит один из пунктов ТЗ)
 */

@DataJpaTest
@Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
class BookingRepositoryTest {
    private static final LocalDateTime TIME_2021_11_04_07H = LocalDateTime.of(2021, 11, 4,
            7, 0, 0, 0);
    private static final LocalDateTime TIME_2021_11_04_20H = LocalDateTime.of(2021, 11, 4,
            20, 50, 27, 9657676);
    private static final LocalDateTime TIME_2022_11_06_20H = LocalDateTime.of(2022, 11, 6,
            20, 50, 27, 9657676);

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void patchStatusOfBooking() {
        Booking booking = bookingRepository.patchStatusOfBooking(2L, true, 1L);
        assertEquals(booking.getStatus(), Status.APPROVED);
    }

    @Test
    void findFirstByItem_IdAndEndIsBeforeOrderByEndDesc() {
        Booking booking = bookingRepository.findFirstByItem_IdAndEndIsBeforeOrderByEndDesc(1L, TIME_2022_11_06_20H);
        assertEquals(booking.getId(), 2);
    }

    @Test
    void findFirstByItem_IdAndStartIsAfterOrderByStartAsc() {
        Booking booking = bookingRepository.findFirstByItem_IdAndStartIsAfterOrderByStartAsc(1L, TIME_2021_11_04_20H);
        assertEquals(booking.getId(), 2);
    }

    @Test
    void existsBookingByItem_IdAndBooker_IdAndEndIsBefore() {
        boolean flag = bookingRepository.existsBookingByItem_IdAndBooker_IdAndEndIsBefore(1, 2, TIME_2022_11_06_20H);
        assertTrue(flag);
    }

    @Test
    void findAllByBookerIdOrderByIdAsc() {
        List<Booking> list = bookingRepository
                .findAllByBookerIdOrderByIdAsc(2, PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 4);

        list = bookingRepository
                .findAllByBookerIdOrderByIdAsc(1, PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }

    @Test
    void findBookingsInPastByBookerIdOrderByIdAsc() {
        List<Booking> list = bookingRepository
                .findBookingsInPastByBookerIdOrderByIdAsc(TIME_2022_11_06_20H, 2,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 2);

        list = bookingRepository
                .findBookingsInPastByBookerIdOrderByIdAsc(TIME_2022_11_06_20H, 5,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }

    @Test
    void findBookingsInFutureByBookerIdOrderByIdAsc() {
        List<Booking> list = bookingRepository
                .findBookingsInFutureByBookerIdOrderByIdAsc(TIME_2022_11_06_20H, 2,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 2);

        list = bookingRepository
                .findBookingsInFutureByBookerIdOrderByIdAsc(TIME_2022_11_06_20H, 1,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }

    @Test
    void findAllByStatusAndBooker_IdOrderByIdDesc() {
        List<Booking> list = bookingRepository
                .findAllByStatusAndBooker_IdOrderByIdDesc(Status.REJECTED, 2,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 1);

        list = bookingRepository
                .findAllByStatusAndBooker_IdOrderByIdDesc(Status.REJECTED, 3,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }

    @Test
    void findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderById() {
        List<Booking> list = bookingRepository
                .findAllByBooker_IdAndStartIsBeforeAndEndIsAfterOrderById(2, TIME_2021_11_04_07H,
                        TIME_2021_11_04_07H, PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }

    @Test
    void findBookingsByStatusAndForOwnerItems() {
        List<Booking> list = bookingRepository
                .findBookingsByStatusAndForOwnerItems(Status.REJECTED, 1,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 1);

        list = bookingRepository.findBookingsByStatusAndForOwnerItems(Status.REJECTED, 2,
                PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }

    @Test
    void findBookingsForOwnerItems() {
        List<Booking> list = bookingRepository
                .findBookingsForOwnerItems(1, PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 4);

        list = bookingRepository
                .findBookingsForOwnerItems(2, PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }

    @Test
    void findBookingsInFutureForOwnerItems() {
        List<Booking> list = bookingRepository
                .findBookingsInFutureForOwnerItems(TIME_2022_11_06_20H, 1,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 2);

        list = bookingRepository
                .findBookingsInFutureForOwnerItems(TIME_2022_11_06_20H, 2,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }

    @Test
    void findCurrentBookingsForOwnerItems() {
        List<Booking> list = bookingRepository
                .findCurrentBookingsForOwnerItems(TIME_2021_11_04_07H, 1,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }

    @Test
    void findBookingsInPastForOwnerItems() {
        List<Booking> list = bookingRepository
                .findBookingsInPastForOwnerItems(TIME_2022_11_06_20H, 1,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 2);

        list = bookingRepository
                .findBookingsInPastForOwnerItems(TIME_2022_11_06_20H, 5,
                        PageRequest.of(0, 100)).getContent();
        assertEquals(list.size(), 0);
    }
}