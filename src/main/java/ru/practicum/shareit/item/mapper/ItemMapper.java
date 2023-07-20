package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto itemToDto(Item item);

    Item itemFromDto(ItemDto itemDto);

    ItemBookingDto itemToItemBookingDto(Item item);

    @Mapping(target = "id", source = "item.id")
    ItemDtoWithBookingsAndComments itemToItemDtoWithBookingAndComments(Item item);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateItemFromDto(ItemDto itemDto, @MappingTarget Item item);
}
