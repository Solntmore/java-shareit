package ru.practicum.shareit;

import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.comment.dto.RequestCommentDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.user.dto.RequestUserDto;

import java.time.LocalDateTime;

public class StaticMethodsAndStringsForTests {

    public static final String RESET_IDS = "alter table users alter column id restart with 1;" +
            "alter table items alter column id restart with 1;" +
            "alter table requests alter column id restart with 1;" +
            "alter table bookings alter column id restart with 1;";
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
