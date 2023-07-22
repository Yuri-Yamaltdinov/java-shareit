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
/*@Component
@RequiredArgsConstructor
public class BookingMapper {
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

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
                .booker(userMapper.userToUserBookingDto(booking.getBooker()))
                .item(itemMapper.itemToItemBookingDto(booking.getItem()))
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
}*/
