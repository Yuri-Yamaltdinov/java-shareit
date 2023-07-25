package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInitial;

import java.util.List;

public interface BookingService {
    BookingDto create(Long userId, BookingDtoInitial bookingDtoInitial);

    BookingDto setStatus(Long userId, Long bookingId, Boolean approved);

    BookingDto findById(Long userId, Long bookingId);

    List<BookingDto> findAllByState(Long userId, String state, Integer from, Integer size);

    List<BookingDto> findAllByItemOwner(Long userId, String state, Integer from, Integer size);
}
