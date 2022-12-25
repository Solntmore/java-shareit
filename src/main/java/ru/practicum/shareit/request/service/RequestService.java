package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ResponseItemForRequestDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.RequestRequestDto;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.exceptions.RequestNotFoundException;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.storage.RequestRepository;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper requestMapper;

    private final ItemMapper itemMapper;


    public ResponseRequestDto createItemRequest(long userId, RequestRequestDto requestRequestDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));
        LocalDateTime createdTime = LocalDateTime.now();

        Request itemRequest = requestMapper.fromRequestItemDtoToItemRequest(requestRequestDto);
        itemRequest.setAuthor(user);
        itemRequest.setCreated(createdTime);

        Request saveRequest = requestRepository.save(itemRequest);
        ResponseRequestDto responseRequestDto =
                requestMapper.fromItemRequestToResponseItemRequestDto(saveRequest);

        return responseRequestDto;
    }

    public List<ResponseRequestDto> getMyItemsRequest(long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));

        return convertToResponseRequestDto(requestRepository.findAllByAuthor_Id(userId));
    }

    public List<ResponseRequestDto> getInfoOfRequest(long userId, PageRequest pageRequest) {

        return convertToResponseRequestDto(
                requestRepository.findAll(pageRequest).getContent()
                        .stream()
                        .filter(request -> request.getAuthor().getId() != userId)
                        .collect(Collectors.toList())
        );
    }

    public ResponseRequestDto getInformationAboutOneRequest(long userId, long requestId) {
        userRepository.findById(userId).orElseThrow(() ->
                new UserNotFoundException("The user with the " + userId + " is not registered."));
        Optional<Request> optionalRequest = requestRepository.findById(requestId);

        if (optionalRequest.isEmpty()) {
            throw new RequestNotFoundException("Request with id " + requestId + "is not  registred");
        }

        ResponseRequestDto responseRequestDto =
                requestMapper.fromItemRequestToResponseItemRequestDto(optionalRequest.get());

        return setItems(responseRequestDto);
    }

    private List<ResponseRequestDto> convertToResponseRequestDto(List<Request> list) {
        return list.stream()
                .map(requestMapper::fromItemRequestToResponseItemRequestDto)
                .map(this::setItems)
                .collect(Collectors.toList());
    }

    private ResponseRequestDto setItems(ResponseRequestDto responseRequestDto) {
        List<ResponseItemForRequestDto> itemsList = itemRepository.findAllByRequestId(responseRequestDto.getId())
                .stream()
                .map(itemMapper::itemToResponseItemForRequestDto)
                .collect(Collectors.toList());

        responseRequestDto.setItems(itemsList);

        return responseRequestDto;
    }
}
