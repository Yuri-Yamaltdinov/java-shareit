package ru.practicum.shareit.item.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @SneakyThrows
    @Test
    void testItemCreateDto() {
        ItemDto itemCreateDto = ItemDto.builder()
                .name("name")
                .description("desc")
                .available(true)
                .requestId(0L)
                .build();

        JsonContent<ItemDto> content = jacksonTester.write(itemCreateDto);

        assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemCreateDto.getName());
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemCreateDto.getDescription());
        assertThat(content).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemCreateDto.getAvailable());
        assertThat(content).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemCreateDto.getRequestId().intValue());
    }

    @SneakyThrows
    @Test
    void testItemDto() {
        ItemDto itemDto = ItemDto.builder()
                .id(0L)
                .name("name")
                .description("desc")
                .available(true)
                .requestId(0L)
                .build();

        JsonContent<ItemDto> content = jacksonTester.write(itemDto);

        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(itemDto.getName());
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemDto.getDescription());
        assertThat(content).extractingJsonPathBooleanValue("$.available")
                .isEqualTo(itemDto.getAvailable());
        assertThat(content).extractingJsonPathNumberValue("$.requestId")
                .isEqualTo(itemDto.getRequestId().intValue());
    }
}
