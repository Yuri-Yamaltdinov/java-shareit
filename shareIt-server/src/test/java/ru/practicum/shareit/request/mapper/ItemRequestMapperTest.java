package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRequestMapperTest {
    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void fromDto() {
        ItemDto itemDto = ItemDto.builder()
                .id(0L)
                .name("ItemDto")
                .description("descDto")
                .available(true)
                .requestId(0L).build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(0L)
                .description("description")
                .created(LocalDateTime.parse("2023-07-20T22:03:23.909930411"))
                .items(List.of(itemDto)).build();

        ItemRequest actualRequest = mapper.itemRequestFromDto(itemRequestDto);

        assertEquals(0L, actualRequest.getId());
        assertEquals("description", actualRequest.getDescription());
        assertEquals(LocalDateTime.parse("2023-07-20T22:03:23.909930411"), actualRequest.getCreated());
        assertFalse(actualRequest.getItems().isEmpty());

        Item actualItem = actualRequest.getItems().get(0);

        assertEquals(0L, actualItem.getId());
        assertEquals("ItemDto", actualItem.getName());
        assertEquals("descDto", actualItem.getDescription());
        assertEquals(true, actualItem.getAvailable());
        assertNull(actualItem.getOwner());
        assertNull(actualItem.getRequest());
    }

    @Test
    void toDto() {
        Item item = Item.builder()
                .id(0L)
                .name("itemName")
                .description("descItem")
                .available(true)
                .owner(new User())
                .request(ItemRequest.builder().id(0L).build())
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(0L)
                .description("desc")
                .created(LocalDateTime.parse("2023-07-20T22:03:23.909930411"))
                .items(List.of(item))
                .build();

        ItemRequestDto actualDto = mapper.itemRequestToDto(itemRequest);

        assertEquals(0L, actualDto.getId());
        assertEquals("desc", actualDto.getDescription());
        assertEquals(LocalDateTime.parse("2023-07-20T22:03:23.909930411"), actualDto.getCreated());
        assertFalse(actualDto.getItems().isEmpty());

        ItemDto actualItemDto = actualDto.getItems().get(0);

        assertEquals(0L, actualItemDto.getId());
        assertEquals("itemName", actualItemDto.getName());
        assertEquals("descItem", actualItemDto.getDescription());
        assertEquals(true, actualItemDto.getAvailable());
        assertEquals(0L, actualItemDto.getRequestId());

    }

    @Test
    void itemToDto() {
        Item item = Item.builder()
                .id(0L)
                .name("itemName")
                .description("descItem")
                .available(true)
                .owner(new User())
                .request(ItemRequest.builder().id(0L).build())
                .build();

        ItemDto actualItemDto = mapper.itemToDto(item);

        assertEquals(0L, actualItemDto.getId());
        assertEquals("itemName", actualItemDto.getName());
        assertEquals("descItem", actualItemDto.getDescription());
        assertEquals(true, actualItemDto.getAvailable());
        assertEquals(0L, actualItemDto.getRequestId());
    }
}
