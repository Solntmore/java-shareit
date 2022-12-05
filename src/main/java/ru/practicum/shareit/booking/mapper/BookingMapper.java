package ru.practicum.shareit.booking.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.RequestBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
public interface BookingMapper {

    Booking responseBookingDtoToBooking(ResponseBookingDto responseBookingDto);

    ResponseBookingDto bookingToResponseBookingDto(Booking booking);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking updateBookingFromResponseBookingDto(ResponseBookingDto responseBookingDto, @MappingTarget Booking booking);

    @Mapping(source = "itemId", target = "item.id")
    Booking requestBookingDtoToBooking(RequestBookingDto requestBookingDto);

    @Mapping(source = "item.id", target = "itemId")
    RequestBookingDto bookingToRequestBookingDto(Booking booking);

    @Mapping(source = "itemId", target = "item.id")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking updateBookingFromRequestBookingDto(RequestBookingDto requestBookingDto, @MappingTarget Booking booking);

    @Mapping(source = "itemId", target = "item.id")
    @Mapping(source = "bookerId", target = "booker.id")
    Booking requestBookingDtoToBooking1(RequestBookingDto requestBookingDto);

    @InheritInverseConfiguration(name = "requestBookingDtoToBooking1")
    RequestBookingDto bookingToRequestBookingDto1(Booking booking);

    @InheritConfiguration(name = "requestBookingDtoToBooking1")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Booking updateBookingFromRequestBookingDto1(RequestBookingDto requestBookingDto, @MappingTarget Booking booking);
}
