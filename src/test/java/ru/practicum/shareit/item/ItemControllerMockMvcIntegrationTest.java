package ru.practicum.shareit.item;

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
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;

import java.util.ArrayList;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ItemControllerMockMvcIntegrationTest {
    private static RequestItemDto item1;
    private static RequestItemDto item2;
    private static RequestItemDto itemWithInvalidName;
    private static RequestItemDto itemWithInvalidDescription;
    private static RequestItemDto itemWithInvalidAvailable;
    private static RequestCommentDto comment1;
    private static RequestCommentDto commentInvalid;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void makeObjects() {
        item1 = makeRequestItemDto("Дрель", "Круто сверлит",
                true, 1);
        item2 = makeRequestItemDto("Дрель", "Нормально сверлит",
                true, 1);
        itemWithInvalidName = makeRequestItemDto(" ", "Не класть на землю",
                true, 3);
        itemWithInvalidDescription = makeRequestItemDto("Грабли", "",
                true, 3);
        itemWithInvalidAvailable = makeRequestItemDto("Грабли", "Не класть на землю",
                null, 3);
        comment1 = makeRequestCommentDto("Крутая вещь");
        commentInvalid = makeRequestCommentDto("");
    }

    @Test
    @DisplayName("Успешное создание вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS})
    public void createItemGetStatus200() throws Exception {

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(item1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Круто сверлит"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1));
    }

    @Test
    @DisplayName("Неуспешное создание вещи  из-за не существующего userId")
    @Sql(statements = {RESET_IDS, CREATE_USERS})
    public void createItemWithNotFoundUserGetStatus404() throws Exception {

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(item1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешное создание вещи из-за не валидного name")
    @Sql(statements = {RESET_IDS, CREATE_USERS})
    public void createItemWithInvalidNameGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(itemWithInvalidName))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Неуспешное создание вещи из-за не валидного description")
    @Sql(statements = {RESET_IDS, CREATE_USERS})
    public void createItemWithInvalidDescriptionGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(itemWithInvalidDescription))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Неуспешное создание вещи из-за не валидного available")
    @Sql(statements = {RESET_IDS, CREATE_USERS})
    public void createItemWithInvalidAvailableGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/items")
                                .content(objectMapper.writeValueAsString(itemWithInvalidAvailable))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Успешный запрос информации о вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getItemGetStatus200() throws Exception {
        ResponseItemDto responseItemDto = new ResponseItemDto(1L, "Дрель", "Круто сверлит",
                true, 1);
        responseItemDto.setLastBooking(new ItemBookingDto(2L, 2L));
        responseItemDto.setNextBooking(new ItemBookingDto(3L, 2L));
        responseItemDto.setComments(new ArrayList<>());

        mockMvc.perform(
                        get("/items/1")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseItemDto)));
    }

    @Test
    @DisplayName("Неуспешный запрос информации о вещи из-за неверного id вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void getItemWithInvalidItemIdGetStatus404() throws Exception {

        mockMvc.perform(
                        get("/items/552")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешный запрос информации о вещи из-за неверного id пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void getItemWithInvalidUserIdGetStatus404() throws Exception {

        mockMvc.perform(
                        get("/items/1")
                                .header("X-Sharer-User-Id", "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешный запрос информации о вещах пользователя с id 1")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void getItemsByOwnerIdGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(item1, item2))));
    }

    @Test
    @DisplayName("Неуспешный запрос информации о вещах пользователя не не валидным id")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void getItemsByInvalidOwnerIdGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/items")
                                .header("X-Sharer-User-Id", "552")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<ResponseItemDto>())));
    }

    @Test
    @DisplayName("Успешный запрос информации о вещах по ключевым словам")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void getItemsByKeyWordsGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/items/search?text=дРелЬ")
                                .header("X-Sharer-User-Id", "3")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Arrays.asList(item1, item2))));
    }

    @Test
    @DisplayName("Запрос информации о вещах по пустым ключевым словам")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void getItemsByEmptyKeyWordsGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/items/search?text= ")
                                .header("X-Sharer-User-Id", "3")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new ArrayList<ResponseItemDto>())));
    }

    @Test
    @DisplayName("Успешное обновление вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void patchItemGetStatus200() throws Exception {

        mockMvc.perform(
                        patch("/items/3")
                                .content(objectMapper.writeValueAsString(item1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Дрель"))
                .andExpect(jsonPath("$.description").value("Круто сверлит"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(2));
    }

    @Test
    @DisplayName("Неуспешное обновление вещи из-за не валидного userId")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void patchItemWithNotFoundUserGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/items/3")
                                .content(objectMapper.writeValueAsString(item1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешное обновление вещи из-за не хозяином вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void patchItemWithByNotOwnerGetStatus403() throws Exception {

        mockMvc.perform(
                        patch("/items/3")
                                .content(objectMapper.writeValueAsString(item1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Неуспешное обновление вещи из-за не валидного имени вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void patchItemWithInvalidNameItemGetStatus409() throws Exception {

        mockMvc.perform(
                        patch("/items/1")
                                .content(objectMapper.writeValueAsString(itemWithInvalidName))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Неуспешное обновление вещи из-за не валидного описания вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void patchItemWithInvalidDescriptionItemGetStatus409() throws Exception {

        mockMvc.perform(
                        patch("/items/1")
                                .content(objectMapper.writeValueAsString(itemWithInvalidDescription))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("Успешное создание комментария к вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void addCommentGetStatus200() throws Exception {

        mockMvc.perform(
                        post("/items/1/comment")
                                .content(objectMapper.writeValueAsString(comment1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.text").value("Крутая вещь"))
                .andExpect(jsonPath("$.authorName").value("konovalov1992@yandex.ru"));
    }

    @Test
    @DisplayName("Неуспешное создание комментария к вещи из-за отсутствия бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void addCommentWithoutBookingGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/items/1/comment")
                                .content(objectMapper.writeValueAsString(comment1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "3")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Неуспешное создание комментария к вещи из-за отсутствия пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void addCommentWithNotFoundUserGetStatus404() throws Exception {

        mockMvc.perform(
                        post("/items/1/comment")
                                .content(objectMapper.writeValueAsString(comment1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешное создание комментария к вещи из-за не валидного комментария")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void addCommentWithInvalidCommentGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/items/1/comment")
                                .content(objectMapper.writeValueAsString(commentInvalid))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isBadRequest());
    }

}
