package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.BookingItemDto;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.dto.ResponseItemForRequestDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface ItemMapper {
    Item toEntity(ResponseItemDto responseItemDto);

    ResponseItemDto itemToResponseItemDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item partialUpdate(ResponseItemDto responseItemDto, @MappingTarget Item item);

    Item requestItemDtoToItem(RequestItemDto requestItemDto);

    RequestItemDto toDto1(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item partialUpdate1(RequestItemDto requestItemDto, @MappingTarget Item item);

    Item toEntity2(BookingItemDto bookingItemDto);

    BookingItemDto toDto2(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item partialUpdate2(BookingItemDto bookingItemDto, @MappingTarget Item item);

    Item toEntity1(ResponseItemForRequestDto responseItemForRequestDto);

    ResponseItemForRequestDto itemToResponseItemForRequestDto(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item partialUpdate3(ResponseItemForRequestDto responseItemForRequestDto, @MappingTarget Item item);
}