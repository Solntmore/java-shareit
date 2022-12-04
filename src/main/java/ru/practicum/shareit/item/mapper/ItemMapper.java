package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.RequestItemDto;
import ru.practicum.shareit.item.dto.ResponseItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ResponseItemDto itemToDto(Item item);

    Item dtoToItem(ResponseItemDto responseItemDto);

    ResponseItemDto itemToRequestDto(Item item);

    Item requestItemDtoToItem(RequestItemDto requestItemDto);
}
