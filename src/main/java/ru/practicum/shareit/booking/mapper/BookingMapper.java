package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInitial;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(target = "id", source = "bookingDtoInitial.id")
    Booking bookingFromDtoInitial(BookingDtoInitial bookingDtoInitial,
                                  User booker, Item item);

    BookingDto bookingToDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingInfoDto bookingToInfoDto(Booking booking);

}
