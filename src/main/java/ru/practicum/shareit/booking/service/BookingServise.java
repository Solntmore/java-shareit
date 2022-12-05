package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.NotAllowedException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServise {

    BookingRepository bookingRepository;

    BookingMapper bookingMapper;

    public ResponseBookingDto createBooking(RequestBookingDto requestBookingDto, long userId) {
        requestBookingDto.setBookerId(userId);

        return bookingMapper.bookingToResponseBookingDto(
                bookingRepository.save(
                        bookingMapper.requestBookingDtoToBooking(requestBookingDto)));
    }

    public ResponseBookingDto findBookingById(long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(()
                -> new BookingNotFoundException("Booking with bookingId " + bookingId + " is not registered."));

        return bookingMapper.bookingToResponseBookingDto(booking);
    }

    public ResponseBookingDto patchStatusOfBooking(long bookingId, boolean approve, long userId) {
        return bookingMapper.bookingToResponseBookingDto(
                bookingRepository.patchStatusOfBooking(bookingId, approve, userId));
    }

    public ResponseBookingDto getInfoOfBooking(long bookingId, long userId) {
        Booking booking = bookingRepository.findById(bookingId).get();

        if (booking.getBooker().getId() == userId) {
            return bookingMapper.bookingToResponseBookingDto(booking);
        }

        if (booking.getItem().getOwner() == userId) {
            return bookingMapper.bookingToResponseBookingDto(booking);
        }

        throw new NotAllowedException("Only booker or item`s owner allow get booking information.");
    }
}
