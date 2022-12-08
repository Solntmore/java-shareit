package ru.practicum.shareit.booking.exception;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidStateException extends MethodArgumentTypeMismatchException {


    public InvalidStateException(Object value, Class<?> requiredType, String name, MethodParameter param, Throwable cause) {
        super(value, requiredType, name, param, cause);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public String getName() {
        return super.getName();
    }

}
