package ru.practicum.shareit.user.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingUserDto;
import ru.practicum.shareit.user.dto.RequestUserDto;
import ru.practicum.shareit.user.dto.ResponseUserDto;
import ru.practicum.shareit.user.model.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface UserMapper {
    User toEntity(ResponseUserDto responseUserDto);

    ResponseUserDto userToResponseUserDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate(ResponseUserDto responseUserDto, @MappingTarget User user);

    User requestDtoToUser(RequestUserDto requestUserDto);

    RequestUserDto toDto1(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate1(RequestUserDto requestUserDto, @MappingTarget User user);

    User toEntity2(BookingUserDto bookingUserDto);

    BookingUserDto toDto2(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User partialUpdate2(BookingUserDto bookingUserDto, @MappingTarget User user);
}