package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.BookingUserDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


@JsonTest
public class BookingSerializationTest {
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testBookingItemDto() throws Exception {
        BookingItemDto bookingItemDto = new BookingItemDto(1L, "Дрель");

        String expectedResult = "{\"id\":1,\"name\":\"Дрель\"}";

        assertEquals(expectedResult, objectMapper.writeValueAsString(bookingItemDto));
    }

    @Test
    public void testBookingUserDto() throws Exception {
        BookingUserDto bookingUserDto = new BookingUserDto(1L);

        String expectedResult = "{\"id\":1}";

        assertEquals(expectedResult, objectMapper.writeValueAsString(bookingUserDto));
    }

    @Test
    public void testResponseBookingDto() throws Exception {
        ResponseBookingDto responseBookingDto = new ResponseBookingDto(1L,
                LocalDateTime.of(2022, 12,25, 17, 50, 27, 9657676),
                LocalDateTime.of(2022, 12,25, 19, 50, 27, 9657676),
                new BookingItemDto(1L, "Дрель"), new BookingUserDto(2L), Status.WAITING);

        String expectedResult = "{\"id\":1,\"start\":\"2022-12-25T17:50:27.009657676\"," +
                "\"end\":\"2022-12-25T19:50:27.009657676\",\"item\":{\"id\":1,\"name\":\"Дрель\"},\"booker\":{\"id\":2}," +
                "\"status\":\"WAITING\"}";

        assertEquals(expectedResult, objectMapper.writeValueAsString(responseBookingDto));
    }
}
