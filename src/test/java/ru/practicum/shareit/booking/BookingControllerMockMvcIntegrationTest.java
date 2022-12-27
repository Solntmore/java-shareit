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
import ru.practicum.shareit.booking.dto.BookingItemDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.dto.BookingUserDto;
import ru.practicum.shareit.user.dto.RequestUserDto;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.practicum.shareit.StaticMethodsAndConstantsForTests.*;
import static ru.practicum.shareit.item.ItemConstants.X_SHADER_USER_ID;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BookingControllerMockMvcIntegrationTest {
    private static RequestBookingDto booking1;
    private static RequestBookingDto booking2;
    private static RequestBookingDto bookingStartInPast;
    private static RequestBookingDto bookingStartBeforeEnd;
    private static RequestBookingDto bookingWithNotFoundItem;
    private static RequestUserDto user1;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @BeforeAll
    public static void makeObjects() {
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
        user1 = makeRequestUserDto("Дмитрий Безвещевой", "noItemNoMoney@yandex.ru");
    }

    @Test
    @DisplayName("Успешное создание бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createBookingGetStatus200() throws Exception {
        ResponseBookingDto responseBookingDto = new ResponseBookingDto(1L, booking1.getStart(), booking1.getEnd(),
                new BookingItemDto(1L, "Дрель"), new BookingUserDto(2L), Status.WAITING);

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(booking1))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(responseBookingDto)));
    }

    @Test
    @DisplayName("Неудачное создание бронирования из-за начала в прошлом")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS})
    public void createBookingWithStartInPastGetStatus400() throws Exception {

        mockMvc.perform(
                        post("/bookings")
                                .content(objectMapper.writeValueAsString(bookingStartInPast))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header(X_SHADER_USER_ID, "2")
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
                                .header(X_SHADER_USER_ID, "2")
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
                                .header(X_SHADER_USER_ID, "1")
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
                                .header(X_SHADER_USER_ID, "2")
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
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Успешное обновление бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingApprovedGetStatus200() throws Exception {

        mockMvc.perform(
                        patch("/bookings/2?approved=true")
                                .header(X_SHADER_USER_ID, "1")
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
                                .header(X_SHADER_USER_ID, "1")
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
    @DisplayName("Неудачное подтверждение бронирование букером")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingByBookerGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/bookings/2?approved=true")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неудачное подтверждение бронирование не владельцем вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingNotByItemsOwnerGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/bookings/2?approved=true")
                                .header(X_SHADER_USER_ID, "3")
                )
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Неудачное обновление бронирования несуществующим пользователем")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingNotFoundUserGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/bookings/2?approved=true")
                                .header(X_SHADER_USER_ID, "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неудачное обновление несуществующего бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingNotFoundBookingGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/bookings/552?approved=true")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неудачное подтверждение уже подтвержденного бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void patchBookingThatAlreadyApprovedGetStatus404() throws Exception {

        mockMvc.perform(
                        patch("/bookings/1?approved=true")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос всех бронирований пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getBookingOfUserGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(2))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].item.id").value(1))
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[2].item.id").value(1))
                .andExpect(jsonPath("$[3].id").isNumber())
                .andExpect(jsonPath("$[3].item.id").value(2));
    }

    @Test
    @DisplayName("Неуспешный запрос бронирований несуществующего пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getBookingForNotFoundUserGetStatus404() throws Exception {

        mockMvc.perform(
                        get("/bookings")
                                .header(X_SHADER_USER_ID, "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Запрос всех прошедших бронирований пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getBookingOfUserInPastGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings?state=PAST")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].start").isNotEmpty())
                .andExpect(jsonPath("$[1].end").isNotEmpty())
                .andExpect(jsonPath("$[1].item.id").value(2))
                .andExpect(jsonPath("$[1].item.name").value("Дрель"))
                .andExpect(jsonPath("$[1].booker.id").value(2))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    @DisplayName("Запрос всех будущих бронирований пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getBookingOfUserInFutureGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings?state=FUTURE")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(2))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].start").isNotEmpty())
                .andExpect(jsonPath("$[1].end").isNotEmpty())
                .andExpect(jsonPath("$[1].item.id").value(1))
                .andExpect(jsonPath("$[1].item.name").value("Дрель"))
                .andExpect(jsonPath("$[1].booker.id").value(2))
                .andExpect(jsonPath("$[1].status").value("REJECTED"));
    }

    @Test
    @DisplayName("Запрос всех ожидающих бронирований пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getBookingOfUserWithStatusWaitingGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings?state=WAITING")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    @DisplayName("Запрос всех отклоненных бронирований пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getBookingOfUserWithStatusRejectedGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings?state=REJECTED")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("REJECTED"));
    }

    @Test
    @DisplayName("Неуспешный запрос всех бронирований пользователя с не валидным статусом")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getBookingOfUserWithInvalidStatusGetStatus400() throws Exception {

        mockMvc.perform(
                        get("/bookings?state=INVALID")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос бронирований по вещи пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getAllBookingsForNotFoundUsersItemsStatusGetStatus404() throws Exception {

        mockMvc.perform(
                        get("/bookings/owner")
                                .header(X_SHADER_USER_ID, "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Запрос бронирований пользователя без вещей")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getAllBookingsForUserWithoutItemsStatusGetStatus404() throws Exception {
        mockMvc.perform(
                post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON)
        );
        mockMvc.perform(
                        get("/bookings/owner")
                                .header(X_SHADER_USER_ID, "4")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неуспешный запрос всех бронирований вещей пользователя с не валидным статусом")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getAllBookingsForUserItemsWithInvalidStatusGetStatus400() throws Exception {

        mockMvc.perform(
                        get("/bookings/owner?state=INVALID")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Запрос всех бронирований вещей пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getAllBookingsForUserItemsGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings/owner")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(2))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].item.id").value(1))
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[2].item.id").value(1))
                .andExpect(jsonPath("$[3].id").isNumber())
                .andExpect(jsonPath("$[3].item.id").value(2));
    }

    @Test
    @DisplayName("Запрос всех прошедших бронирований вещей пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getAllBookingsForUserItemsInPastGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings/owner?state=PAST")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("WAITING"))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].start").isNotEmpty())
                .andExpect(jsonPath("$[1].end").isNotEmpty())
                .andExpect(jsonPath("$[1].item.id").value(2))
                .andExpect(jsonPath("$[1].item.name").value("Дрель"))
                .andExpect(jsonPath("$[1].booker.id").value(2))
                .andExpect(jsonPath("$[1].status").value("APPROVED"));
    }

    @Test
    @DisplayName("Запрос всех будущих бронирований вещей пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getAllBookingsForUserItemsInFutureGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings/owner?state=FUTURE")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(2))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[1].start").isNotEmpty())
                .andExpect(jsonPath("$[1].end").isNotEmpty())
                .andExpect(jsonPath("$[1].item.id").value(1))
                .andExpect(jsonPath("$[1].item.name").value("Дрель"))
                .andExpect(jsonPath("$[1].booker.id").value(2))
                .andExpect(jsonPath("$[1].status").value("REJECTED"));
    }

    @Test
    @DisplayName("Запрос всех ожидающих бронирований вещей пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getAllWaitingBookingsForUserItemsGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings/owner?state=WAITING")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("WAITING"));
    }

    @Test
    @DisplayName("Запрос всех отклоненных бронирований вещей пользователя")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getAllRejectedBookingsForUserItemsGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings/owner?state=REJECTED")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].start").isNotEmpty())
                .andExpect(jsonPath("$[0].end").isNotEmpty())
                .andExpect(jsonPath("$[0].item.id").value(1))
                .andExpect(jsonPath("$[0].item.name").value("Дрель"))
                .andExpect(jsonPath("$[0].booker.id").value(2))
                .andExpect(jsonPath("$[0].status").value("REJECTED"));
    }

    @Test
    @DisplayName("Запрос конкретного бронирования владельцем вещи")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getInfoOfBookingForOwnerGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings/1")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item.id").value(2))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.booker.id").value(2))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Запрос конкретного бронирования автором бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getInfoOfBookingForBookerGetStatus200() throws Exception {

        mockMvc.perform(
                        get("/bookings/1")
                                .header(X_SHADER_USER_ID, "2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.start").isNotEmpty())
                .andExpect(jsonPath("$.end").isNotEmpty())
                .andExpect(jsonPath("$.item.id").value(2))
                .andExpect(jsonPath("$.item.name").value("Дрель"))
                .andExpect(jsonPath("$.booker.id").value(2))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @DisplayName("Неудачный запрос конкретного бронирования не пользователем и не букером")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getInfoOfBookingForOtherUserGetStatus404() throws Exception {

        mockMvc.perform(
                        get("/bookings/1")
                                .header(X_SHADER_USER_ID, "3")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неудачный запрос конкретного бронирования не существующим пользователем")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getInfoOfBookingForNotFoundUserGetStatus404() throws Exception {

        mockMvc.perform(
                        get("/bookings/1")
                                .header(X_SHADER_USER_ID, "552")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Неудачный запрос не существующего бронирования")
    @Sql(statements = {RESET_IDS, CREATE_USERS, CREATE_ITEMS, CREATE_BOOKINGS})
    public void getInfoOfNotFoundBookingGetStatus404() throws Exception {

        mockMvc.perform(
                        get("/bookings/552")
                                .header(X_SHADER_USER_ID, "1")
                )
                .andExpect(status().isNotFound());
    }
}
