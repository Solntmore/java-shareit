package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {ItemMapper.class, UserMapper.class})
public interface BookingMapper {
    Booking toEntityFromResponseBookingDto(ResponseBookingDto responseBookingDto);

    ResponseBookingDto bookingToResponseBookingDto(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking partialUpdate(ResponseBookingDto responseBookingDto, @MappingTarget Booking booking);

    @Mapping(source = "bookerId", target = "booker.id")
    @Mapping(source = "itemId", target = "item.id")
    Booking requestBookingDtoToBooking(RequestBookingDto requestBookingDto);

    @InheritInverseConfiguration(name = "requestBookingDtoToBooking")
    RequestBookingDto toDto1(Booking booking);

    @InheritConfiguration(name = "requestBookingDtoToBooking")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking partialUpdate1(RequestBookingDto requestBookingDto, @MappingTarget Booking booking);

    @Mapping(source = "bookerId", target = "booker.id")
    Booking toEntity(ItemBookingDto itemBookingDto);

    @Mapping(source = "booker.id", target = "bookerId")
    ItemBookingDto bookingToItemBookingDto(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "bookerId", target = "booker.id")
    Booking partialUpdate2(ItemBookingDto itemBookingDto, @MappingTarget Booking booking);
}