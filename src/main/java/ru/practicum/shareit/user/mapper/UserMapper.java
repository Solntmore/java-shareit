package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    ResponseUserDto userToDto(User user);

    User dtoToItem(ResponseUserDto responseUserDto);

    RequestUserDto userToRequestDto(User user);

    User requestDtoToUser(RequestUserDto requestUserDto);
}
