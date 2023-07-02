package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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
}
