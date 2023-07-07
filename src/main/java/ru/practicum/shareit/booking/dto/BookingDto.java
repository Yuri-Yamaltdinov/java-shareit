package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserBookingDto;

import java.time.LocalDateTime;

public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingState status;
    private UserBookingDto booker;
    private ItemBookingDto item;
}
