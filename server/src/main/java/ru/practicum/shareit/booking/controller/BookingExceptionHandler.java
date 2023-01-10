package ru.practicum.shareit.booking.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.booking.model.ErrorResponse;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class BookingExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalRequestState(final MethodArgumentTypeMismatchException e) {
        String error = "Unknown " + e.getName() + ": " + e.getValue();
        log.warn(error);
        return new ErrorResponse(error);
    }
}
