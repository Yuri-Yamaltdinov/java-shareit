package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoInitial {
    private Long id;
    @Positive(message = "ItemId has to be positive")
    private Long itemId;

    @NotNull(message = "Wrong booking start date")
    @FutureOrPresent(message = "Wrong booking start date")
    private LocalDateTime start;

    @NotNull(message = "Wrong booking end date")
    @Future(message = "Wrong booking end date")
    private LocalDateTime end;
}
