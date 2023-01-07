package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import ru.practicum.shareit.user.dto.ResponseUserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserSerializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testResponseUserDto() throws Exception {
        ResponseUserDto responseUserDto = new ResponseUserDto(1L, "Олег",
                "bolshakov2022@yandex.ru");

        String expectedResult = "{\"id\":1,\"name\":\"Олег\",\"email\":\"bolshakov2022@yandex.ru\"}";

        assertEquals(expectedResult, objectMapper.writeValueAsString(responseUserDto));
    }
}
