package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInitial;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "id", source = "bookingDtoInitial.id")
    Booking bookingFromDtoInitial(BookingDtoInitial bookingDtoInitial);

    BookingDto bookingToDto(Booking booking);

    UserBookingDto userToDto(User user);

    ItemBookingDto itemToDto(Item item);

    BookingInfoDto bookingToInfoDto(Booking booking);

}
