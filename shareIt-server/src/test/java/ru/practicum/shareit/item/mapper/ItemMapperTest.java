package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

public class ItemMapperTest {
    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    private Item item;

    @BeforeEach
    void beforeEach() {
        item = Item.builder()
                .id(0L)
                .name("Name")
                .description("desc")
                .available(false)
                .owner(new User())
                .request(ItemRequest.builder().id(0L).build())
                .build();
    }

    @Test
    void itemToDto() {
        ItemDto itemDto = mapper.itemToDto(item);

        assertEquals(0L, itemDto.getId());
        assertEquals("Name", itemDto.getName());
        assertEquals("desc", itemDto.getDescription());
        assertFalse(itemDto.getAvailable());
        assertEquals(0L, itemDto.getRequestId());
    }

    @Test
    void itemFromDto() {
        ItemDto itemDto = ItemDto.builder()
                .id(0L)
                .name("Name")
                .description("desc")
                .available(false)
                .requestId(0L)
                .build();

        Item item = mapper.itemFromDto(itemDto);

        assertEquals(0L, item.getId());
        assertEquals("Name", item.getName());
        assertEquals("desc", item.getDescription());
        assertFalse(item.getAvailable());
        assertNull(item.getOwner());
        assertNull(item.getRequest());
    }

    @Test
    void itemToItemBooked() {
        ItemDtoWithBookingsAndComments itemBooked = mapper.itemToItemDtoWithBookingAndComments(item);

        assertEquals(0L, itemBooked.getId());
        assertEquals("Name", itemBooked.getName());
        assertEquals("desc", itemBooked.getDescription());
        assertFalse(itemBooked.getAvailable());
    }

    @Test
    void updateItemFromDto_whenIdUpdate_thenIdNotUpdate() {
        ItemDto updateDto = ItemDto.builder()
                .id(1L).build();
        mapper.updateItemFromDto(updateDto, item);

        assertEquals(0L, item.getId());
        assertEquals("Name", item.getName());
        assertEquals("desc", item.getDescription());
        assertEquals(false, item.getAvailable());
    }

    @Test
    void updateItemFromDto_whenNameOnly_thenDescriptionAndAvailableNotUpdate() {
        ItemDto updateDto = ItemDto.builder()
                .name("Update").build();
        mapper.updateItemFromDto(updateDto, item);

        assertEquals(0L, item.getId());
        assertEquals("Update", item.getName());
        assertEquals("desc", item.getDescription());
        assertEquals(false, item.getAvailable());
    }

    @Test
    void updateItemFromDto_whenDescriptionOnly_thenNameAndAvailableNotUpdate() {
        ItemDto updateDto = ItemDto.builder()
                .description("Update").build();
        mapper.updateItemFromDto(updateDto, item);

        assertEquals(0L, item.getId());
        assertEquals("Name", item.getName());
        assertEquals("Update", item.getDescription());
        assertEquals(false, item.getAvailable());
    }

    @Test
    void updateItemFromDto_whenAvailableOnly_thenDescriptionAndNameNotUpdate() {
        ItemDto updateDto = ItemDto.builder()
                .available(true).build();
        mapper.updateItemFromDto(updateDto, item);

        assertEquals(0L, item.getId());
        assertEquals("Name", item.getName());
        assertEquals("desc", item.getDescription());
        assertEquals(true, item.getAvailable());
    }

    @Test
    void updateItemFromDto_whenNameNull_thenDescriptionAndAvailableUpdate() {
        ItemDto updateDto = ItemDto.builder()
                .description("Update")
                .available(true).build();
        mapper.updateItemFromDto(updateDto, item);

        assertEquals(0L, item.getId());
        assertEquals("Name", item.getName());
        assertEquals("Update", item.getDescription());
        assertEquals(true, item.getAvailable());
    }

    @Test
    void updateItemFromDto_whenDescriptionNull_thenNameAndAvailableUpdate() {
        ItemDto updateDto = ItemDto.builder()
                .name("Update")
                .available(true).build();
        mapper.updateItemFromDto(updateDto, item);

        assertEquals(0L, item.getId());
        assertEquals("Update", item.getName());
        assertEquals("desc", item.getDescription());
        assertEquals(true, item.getAvailable());
    }

    @Test
    void updateItemFromDto_whenAvailableNull_thenDescriptionAndNameUpdate() {
        ItemDto updateDto = ItemDto.builder()
                .name("Update")
                .description("Update").build();
        mapper.updateItemFromDto(updateDto, item);

        assertEquals(0L, item.getId());
        assertEquals("Update", item.getName());
        assertEquals("Update", item.getDescription());
        assertEquals(false, item.getAvailable());
    }
}
