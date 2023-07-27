package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoInitialTest {
    @Autowired
    private JacksonTester<BookingDtoInitial> jacksonTester;

    @SneakyThrows
    @Test
    void testBookingCreationDto() {
        BookingDtoInitial bookingCreationDto = BookingDtoInitial.builder()
                .itemId(0L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .build();

        JsonContent<BookingDtoInitial> content = jacksonTester.write(bookingCreationDto);

        assertThat(content).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(bookingCreationDto.getItemId().intValue());
    }
}
