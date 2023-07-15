package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoInitial {
    private Long id;
    @PositiveOrZero(message = "ItemId has to be positive number")
    private Long itemId;

    @NotNull(message = "Wrong booking start date")
    @FutureOrPresent(message = "Wrong booking start date")
    private LocalDateTime start;

    @NotNull(message = "Wrong booking end date")
    @Future(message = "Wrong booking end date")
    private LocalDateTime end;
}
