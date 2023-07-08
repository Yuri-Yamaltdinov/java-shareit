package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@UtilityClass
public class ItemMapper {
    public ItemDto toDto(Item item) {
        if (item == null) {
            throw new ValidationException("User entity is null");
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                //.requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .build();
    }

    public Item fromDto(ItemDto itemDto) {
        if (itemDto == null) {
            throw new ValidationException("User entity is null");
        }
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemBookingDto itemToItemBookingDto(Item item) {
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public ItemDtoWithBookingsAndComments toItemDtoWithBookingAndComments(
            Item item,
            BookingInfoDto lastBooking,
            BookingInfoDto nextBooking,
            List<CommentDto> comments
    ) {
        return ItemDtoWithBookingsAndComments.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .comments(comments)
                .build();
    }
}
