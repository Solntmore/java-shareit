package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.RequestItemDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.*;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.CREATE_USERS;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingControllerMockMvcIntegrationTest {
    private static RequestItemDto item1;
    private static RequestItemDto item2;
    private static RequestItemDto itemWithInvalidName;
    private static RequestItemDto itemWithInvalidDescription;
    private static RequestItemDto itemWithInvalidAvailable;
    private static RequestCommentDto comment1;
    private static RequestCommentDto commentInvalid;
    private static RequestBookingDto booking1;
    private static RequestBookingDto booking2;
    private static RequestBookingDto bookingStartInPast;
    private static RequestBookingDto bookingStartBeforeEnd;
    private static RequestBookingDto bookingWithNotFoundItem;

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
        itemWithInvalidName = makeRequestItemDto("", "Не класть на землю",
                true, 3);
        itemWithInvalidDescription = makeRequestItemDto("Грабли", "",
                true, 3);
        itemWithInvalidAvailable = makeRequestItemDto("Грабли", "Не класть на землю",
                null, 3);
        comment1 = makeRequestCommentDto("Крутая вещь");
        commentInvalid = makeRequestCommentDto("");
        booking1 = makeRequestBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3),
                1L, 2L);
        booking2 = makeRequestBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3),
                5L, 2L);
        bookingStartInPast = makeRequestBookingDto(LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(3),
                1L, 2L);
        bookingStartBeforeEnd = makeRequestBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusMinutes(50),
                1L, 2L);
        bookingWithNotFoundItem = makeRequestBookingDto(LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(3),
                50L, 2L);
    }

    @Test
    @DisplayName("Успешное создание бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createBookingGetStatus200() throws Exception {

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(booking1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.booker.id").value(2))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    @DisplayName("Неудачное создание бронирования из-за начала в прошлом")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createBookingWithStartInPastGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingStartInPast))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Неудачное создание бронирования из-за конца раньше старта")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createBookingWithEndBeforeStartGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingStartBeforeEnd))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Неудачное создание бронирования хозяином вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createBookingOwnerOfItemGetStatus404() throws Exception {

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(booking1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неудачное создание бронирования из-за недоступности вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_UNAVAILABLE_ITEMS})
    public void createBookingForUnavailableItemGetStatus404() throws Exception {

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(booking2))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Неудачное создание бронирования несуществующей вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createNotFoundItemGetStatus404() throws Exception {

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingWithNotFoundItem))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное обновление бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingApprovedGetStatus200() throws Exception {

        mockMvc.perform(
                        patch("/bookings/2?approved=true")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.booker.id").value(2))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Успешное обновление бронирования - отказ")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingRejectedGetStatus200() throws Exception {

        mockMvc.perform(
                        patch("/bookings/2?approved=false")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item.id").value(1))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.booker.id").value(2))
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    @DisplayName("Неудачное обновление бронирования не владельцем вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingNotByOwnerGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/bookings/2?approved=true")
                                .header("X-Sharer-User-Id", "2")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неудачное обновление бронирования несуществующим пользователем")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingNotFoundUserGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/bookings/2?approved=true")
                                .header("X-Sharer-User-Id", "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неудачное обновление несуществующего бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingNotFoundBookingGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/bookings/552?approved=true")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неудачное обновление несуществующего бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingThatAlreadyApprovedGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/bookings/1?approved=true")
                                .header("X-Sharer-User-Id", "1")
                )
                .andExpect(status().isBadRequest());
    }
}
