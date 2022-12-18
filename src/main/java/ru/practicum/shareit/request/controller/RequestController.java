package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

import static ru.practicum.shareit.Convert.toPageRequest;
import static ru.practicum.shareit.item.ItemConstants.X_SHADER_USER_ID;

/**
 * TODO Sprint add-item-requests.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
@Validated
public class RequestController {

    private final RequestService requestService;


    @PostMapping
    public ResponseRequestDto createItemRequest(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                @RequestBody @Valid RequestRequestDto requestRequestDto) {
        log.debug("A Post/requests request was received. Create a request {} with userId id {}.", requestRequestDto,
                userId);

        return requestService.createItemRequest(userId, requestRequestDto);
    }

    @GetMapping
    public List<ResponseRequestDto> getMyItemsRequest(@RequestHeader(X_SHADER_USER_ID) long userId) {
        log.debug("A Get/requests request was received. Get information of user`s requests " +
                "for user with id {}", userId);

        return requestService.getMyItemsRequest(userId);
    }

    @GetMapping("/all")
    public List<ResponseRequestDto> getInfoOfRequest(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                     @RequestParam(required = false, defaultValue = "0")
                                                     @Min(0) int from,
                                                     @RequestParam(required = false, defaultValue = "100")
                                                     @Min(0) int size) {
        log.debug("A Get/requests/all?from={}&size={} request was received. Get information of other user`s requests " +
                "for user with id {}", from, size, userId);

        return requestService.getInfoOfRequest(userId, toPageRequest(from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseRequestDto getInformationAboutOneRequest(@RequestHeader(X_SHADER_USER_ID) long userId,
                                                            @PathVariable @Positive long requestId) {
        log.debug("A Get/requests request was received. Get information of request with id {} for user with id {}.",
                userId, requestId);

        return requestService.getInformationAboutOneRequest(userId, requestId);
    }


}
