package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInitial;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public Booking bookingFromDtoInitial(BookingDtoInitial bookingDtoInitial,
                                         User booker, Item item) {
        if (bookingDtoInitial == null) {
            throw new ValidationException("Booking entity is null");
        }
        return Booking.builder()
                .item(item)
                .booker(booker)
                .start(bookingDtoInitial.getStart())
                .end(bookingDtoInitial.getEnd())
                .build();
    }

    public BookingDto bookingToDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .booker(UserMapper.userToUserBookingDto(booking.getBooker()))
                .item(ItemMapper.itemToItemBookingDto(booking.getItem()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    public BookingInfoDto bookingToInfoDto(Booking booking) {
        return BookingInfoDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
