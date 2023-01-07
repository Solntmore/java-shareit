package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.user.dto.RequestUserDto;

import java.time.LocalDateTime;

public class StaticMethodsAndConstantsForTests {

    public static final String RESET_IDS = "alter table users alter column id restart with 1;" +
            "alter table items alter column id restart with 1;" +
            "alter table requests alter column id restart with 1;" +
            "alter table bookings alter column id restart with 1;" +
            "alter table comments alter column id restart with 1;";

    public static final String CREATE_USERS = "INSERT INTO users (email, name) " +
            "VALUES ('Олег', 'bolshakov2022@yandex.ru'), " +
            "       ('Максим', 'konovalov1992@yandex.ru'), " +
            "       ('Ева', 'eva2017@yandex.ru');";

    public static final String CREATE_ITEMS = "INSERT INTO items (is_available, description, name, owner_id, request_id) " +
            "VALUES ('true', 'Круто сверлит', 'Дрель', 1, 1), " +
            "       ('true', 'Нормально сверлит', 'Дрель', 1, 1), " +
            "       ('true', 'Копает землю', 'Лопата', 2, 2), " +
            "       ('true', 'Не класть на землю', 'Грабли', 2, 3); ";

    public static final String CREATE_UNAVAILABLE_ITEMS = "INSERT INTO items (is_available, description, name, owner_id, request_id) " +
            "VALUES ('false', 'Убирает ковер', 'Пылесос', 1, 1), " +
            "       ('false', 'Сам ездит и убирается', 'Робот пылесос', 1, 1);";

    public static final String CREATE_BOOKINGS = "INSERT INTO bookings (end_date, start_date, status, booker_id, item_id) " +
            "VALUES ('2021-11-04 06:00:00', '2021-11-05 06:00:00', 'APPROVED', 2, 2), " +
            "       ('2022-11-04 06:00:00', '2022-11-05 06:00:00', 'WAITING', 2, 1), " +
            "       ('2023-11-04 06:00:00', '2023-11-05 06:00:00', 'REJECTED', 2, 1), " +
            "       ('2024-11-04 06:00:00', '2024-11-05 06:00:00', 'APPROVED', 2, 2); ";
    public static final String CREATE_REQUESTS = "INSERT INTO requests (created, description, author_id) " +
            "VALUES ('2021-11-04 06:00:00', 'Хочу дрель сверлить', 1), " +
            "       ('2022-11-05 06:00:00', 'Нужна лопата, чтобы копать', 1), " +
            "       ('2022-11-06 06:00:00', 'Машинка коллекционная порше', 2), " +
            "       ('2022-11-07 06:00:00', 'Что-то, что сильно бьет и хорошо режет', 2); ";

    public static RequestUserDto makeRequestUserDto(String name, String email) {
        RequestUserDto dto = new RequestUserDto();
        dto.setName(name);
        dto.setEmail(email);

        return dto;
    }

    public static RequestRequestDto makeRequestRequestDto(String description) {
        RequestRequestDto dto = new RequestRequestDto();
        dto.setDescription(description);

        return dto;
    }

    public static RequestItemDto makeRequestItemDto(String name, String description, Boolean available, long requestId) {
        RequestItemDto dto = new RequestItemDto();
        dto.setName(name);
        dto.setDescription(description);
        dto.setAvailable(available);
        dto.setRequestId(requestId);

        return dto;
    }

    public static RequestBookingDto makeRequestBookingDto(LocalDateTime start, LocalDateTime end, Long itemId, Long bookerId) {
        RequestBookingDto dto = new RequestBookingDto();
        dto.setStart(start);
        dto.setEnd(end);
        dto.setItemId(itemId);
        dto.setBookerId(bookerId);

        return dto;
    }

    public static RequestCommentDto makeRequestCommentDto(String text) {
        RequestCommentDto dto = new RequestCommentDto();
        dto.setText(text);

        return dto;
    }
}
