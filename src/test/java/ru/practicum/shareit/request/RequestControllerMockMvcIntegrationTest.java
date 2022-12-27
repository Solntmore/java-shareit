package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.*;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.CREATE_USERS;
import static ru.practicum.shareit.item.ItemConstants.X_SHADER_USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class RequestControllerMockMvcIntegrationTest {

    private static RequestRequestDto request1;
    private static RequestRequestDto request2;
    private static RequestRequestDto request3;
    private static RequestRequestDto requestWithInvalidDescription;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void makeObjects() {
        request1 = makeRequestRequestDto("Нужно лопата, которая копает снег");
        request2 = makeRequestRequestDto("Нужна лопата, чтобы копать");
        request3 = makeRequestRequestDto("Хочу дрель сверлить");
        requestWithInvalidDescription = makeRequestRequestDto("");
    }

    @Test
    @DisplayName("Успешное получение списка своих запросов")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_REQUESTS})
    public void getUserRequestListGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/requests")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].created").value("2021-11-04T06:00:00"))
                .andExpect(jsonPath("$[0].description").value("Хочу дрель сверлить"))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].created").value("2022-11-05T06:00:00"))
                .andExpect(jsonPath("$[1].description").value("Нужна лопата, чтобы копать"));
    }

    @Test
    @DisplayName("Успешное получение списка своих запросов для пользователя без запросов")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_REQUESTS})
    public void getUserRequestListWithoutRequestsGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/requests")
                                .header(X_SHADER_USER_ID, "3")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<ResponseRequestDto>())));
    }

    @Test
    @DisplayName("Неуспешное получение списка запросов не существующим пользователем.")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_REQUESTS})
    public void getNotFoundUserRequestListGetStatus404() throws Exception {

        mockMvc.perform(
                        get("/requests")
                                .header(X_SHADER_USER_ID, "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное создание запроса")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createItemGetStatus200() throws Exception {

        mockMvc.perform(
                        post("/requests")
                                .content(objectMapper.writeValueAsString(request1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.created").isNotEmpty())
                .andExpect(jsonPath("$.description").value("Нужно лопата, которая копает снег"));
    }

    @Test
    @DisplayName("Неуспешное создание запроса с пустым описанием")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createItemWithEmptyDescriptionGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/requests")
                                .content(objectMapper.writeValueAsString(requestWithInvalidDescription))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Неуспешное создание запроса несуществующим пользователем")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createItemByNotFoundUserGetStatus404() throws Exception {

        mockMvc.perform(
                        post("/requests")
                                .content(objectMapper.writeValueAsString(request1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHADER_USER_ID, "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешный запрос информации о запросе вещи.")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_REQUESTS})
    public void getInfoAboutRequestByIdGetStatus200() throws Exception {
        mockMvc.perform(
                        get("/requests/1")
                                .header(X_SHADER_USER_ID, "3")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.created").value("2021-11-04T06:00:00"))
                .andExpect(jsonPath("$.description").value("Хочу дрель сверлить"))
                .andExpect(jsonPath("$.items.length()").value(2));
    }

    @Test
    @DisplayName("Неуспешный запрос информации о запросе вещи несуществующим пользователем.")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_REQUESTS})
    public void getInfoAboutRequestByNotFoundUserGetStatus404() throws Exception {
        mockMvc.perform(
                        get("/requests/552")
                                .header(X_SHADER_USER_ID, "3")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешный запрос информации о несуществующем запросе вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_REQUESTS})
    public void getInfoAboutNotFoundRequestRequestGetStatus404() throws Exception {
        mockMvc.perform(
                        get("/requests/1")
                                .header(X_SHADER_USER_ID, "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное получение списка чужих запросов")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_REQUESTS})
    public void getOtherUsersRequestGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/requests/all")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(request2, request3))));
    }
}
