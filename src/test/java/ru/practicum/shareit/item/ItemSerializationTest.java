package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testItemBookingDto() throws Exception {
        ItemBookingDto itemBookingDto = new ItemBookingDto(1L, 1L);

        String expectedResult = "{\"id\":1,\"bookerId\":1}";

        assertEquals(expectedResult, objectMapper.writeValueAsString(itemBookingDto));
    }

    @Test
    public void testResponseItemDto() throws Exception {
        ResponseItemDto responseItemDto = new ResponseItemDto(1L, "Дрель", "Круто сверлит",
                true, 1);
        responseItemDto.setLastBooking(new ItemBookingDto(2L, 2L));
        responseItemDto.setNextBooking(new ItemBookingDto(3L, 2L));
        responseItemDto.setComments(new ArrayList<>());

        String expectedResult = "{\"id\":1,\"name\":\"Дрель\",\"description\":\"Круто сверлит\",\"available\":true," +
                "\"requestId\":1,\"lastBooking\":{\"id\":2,\"bookerId\":2},\"nextBooking\":{\"id\":3,\"bookerId\":2}," +
                "\"comments\":[]}";

        assertEquals(expectedResult, objectMapper.writeValueAsString(responseItemDto));
    }

}
