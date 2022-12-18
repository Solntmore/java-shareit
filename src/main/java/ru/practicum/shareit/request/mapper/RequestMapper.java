package ru.practicum.shareit.request.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.request.dto.ResponseRequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.dto.RequestRequestDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface RequestMapper {
    Request fromRequestItemDtoToItemRequest(RequestRequestDto requestRequestDto);

    RequestRequestDto toDto(Request request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Request partialUpdate(RequestRequestDto requestRequestDto, @MappingTarget Request request);

    Request toEntity1(ResponseRequestDto responseRequestDto);

    ResponseRequestDto fromItemRequestToResponseItemRequestDto(Request request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Request partialUpdate1(ResponseRequestDto responseRequestDto, @MappingTarget Request request);
}