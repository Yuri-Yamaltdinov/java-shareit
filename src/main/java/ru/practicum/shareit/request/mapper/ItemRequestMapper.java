package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {
    ItemRequest itemRequestFromDto(ItemRequestDto itemRequestDto);

    ItemRequestDto itemRequestToDto(ItemRequest itemRequest);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto itemToDto(Item item);

}
