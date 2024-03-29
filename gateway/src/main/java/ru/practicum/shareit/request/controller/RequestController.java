package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.client.RequestClient;
import ru.practicum.shareit.request.dto.RequestRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import static ru.practicum.shareit.Constants.X_SHADER_USER_ID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class RequestController {

    private final RequestClient requestClient;


    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                    @RequestBody @Valid RequestRequestDto requestRequestDto) {
        log.debug("A Post/requests request was received. Create a request {} with userId id {}.", requestRequestDto,
                userId);

        return requestClient.createItemRequest(userId, requestRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getMyItemsRequest(@RequestHeader(X_SHADER_USER_ID) long userId) {
        log.debug("A Get/requests request was received. Get information of user`s requests " +
                "for user with id {}", userId);

        return requestClient.getMyItemsRequest(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getInfoOfRequest(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                     @RequestParam(required = false, defaultValue = "0")
                                                     @Min(0) int from,
                                                     @RequestParam(required = false, defaultValue = "100")
                                                     @Min(0) int size) {
        log.debug("A Get/requests/all?from={}&size={} request was received. Get information of other user`s requests " +
                "for user with id {}", from, size, userId);

        return requestClient.getInfoOfRequest(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getInformationAboutOneRequest(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                            @PathVariable @Positive long requestId) {
        log.debug("A Get/requests request was received. Get information of request with id {} for user with id {}.",
                userId, requestId);

        return requestClient.getInformationAboutOneRequest(userId, requestId);
    }


}
