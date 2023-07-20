package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @SneakyThrows
    @Test
    void testCreationRequestDto() {
        ItemRequestDto creationRequestDto = ItemRequestDto.builder()
                .description("desc")
                .build();

        JsonContent<ItemRequestDto> content = jacksonTester.write(creationRequestDto);

        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(creationRequestDto.getDescription());
    }

    @SneakyThrows
    @Test
    void testRequestDto() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .created(LocalDateTime.parse("2023-07-20T22:14:51.188116511").minusHours(1))
                .description("desc")
                .id(0L)
                .items(List.of(ItemDto.builder()
                        .id(1L)
                        .requestId(0L)
                        .name("item")
                        .description("description")
                        .available(true)
                        .build()))
                .build();

        JsonContent<ItemRequestDto> content = jacksonTester.write(itemRequestDto);

        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(itemRequestDto.getDescription());
        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(itemRequestDto.getId().intValue());
        assertThat(content).extractingJsonPathNumberValue("$.items[0].id")
                .isEqualTo(itemRequestDto.getItems().get(0).getId().intValue());
        assertThat(content).extractingJsonPathNumberValue("$.items[0].requestId")
                .isEqualTo(itemRequestDto.getItems().get(0).getRequestId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo(itemRequestDto.getItems().get(0).getName());
        assertThat(content).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo(itemRequestDto.getItems().get(0).getDescription());
        assertThat(content).extractingJsonPathBooleanValue("$.items[0].available")
                .isEqualTo(itemRequestDto.getItems().get(0).getAvailable());
    }

    @SneakyThrows
    @Test
    void testRequestNoItemsDto() {
        ItemRequestDto requestNoItemsDto = ItemRequestDto.builder()
                .id(0L)
                .created(LocalDateTime.parse("2023-07-20T22:14:51.188116511").minusHours(2))
                .description("desc")
                .build();

        JsonContent<ItemRequestDto> content = jacksonTester.write(requestNoItemsDto);

        assertThat(content).extractingJsonPathNumberValue("$.id")
                .isEqualTo(requestNoItemsDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.created")
                .isEqualTo(requestNoItemsDto.getCreated().toString());
        assertThat(content).extractingJsonPathStringValue("$.description")
                .isEqualTo(requestNoItemsDto.getDescription());
    }
}
