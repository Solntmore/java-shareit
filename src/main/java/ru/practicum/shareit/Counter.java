package ru.practicum.shareit;

import org.springframework.stereotype.Component;

@Component
public class Counter {
    private long id = 0;

    public long increaseId() {
        return ++id;
    }
}
