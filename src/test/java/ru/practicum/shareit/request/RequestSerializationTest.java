package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.request.dto.ResponseItemForRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class RequestSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testResponseItemForRequestDto() throws Exception {
        ResponseItemForRequestDto responseItemForRequestDto = new ResponseItemForRequestDto(1L, "Дрель",
                "Сверлит", true, 1L);

        String expectedResult = "{\"id\":1,\"name\":\"Дрель\",\"description\":\"Сверлит\",\"available\":true," +
                "\"requestId\":1}";

        assertEquals(expectedResult, objectMapper.writeValueAsString(responseItemForRequestDto));
    }

    @Test
    public void testResponseRequestDto() throws Exception {
        ResponseRequestDto responseRequestDto = new ResponseRequestDto(1L, "Сверлит",
                LocalDateTime.of(2022, 12,25, 17, 50, 27, 9657676));
        responseRequestDto.setItems(new ArrayList<>());
        String expectedResult = "{\"id\":1,\"description\":\"Сверлит\",\"created\":\"2022-12-25T17:50:27.009657676\"," +
                "\"items\":[]}";

        assertEquals(expectedResult, objectMapper.writeValueAsString(responseRequestDto));
    }
}
