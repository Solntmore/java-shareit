package ru.practicum.shareit;

import org.springframework.data.domain.PageRequest;

public final class Convert {
    public static PageRequest toPageRequest(int from, int size) {
        return PageRequest.of((from / size), size);
    }
}
