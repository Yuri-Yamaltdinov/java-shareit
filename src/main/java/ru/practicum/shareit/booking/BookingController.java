package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInitial;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody @Valid BookingDtoInitial bookingDtoInitial) {
        log.info("Got request to POST booking {}", bookingDtoInitial);
        return bookingService.create(userId, bookingDtoInitial);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto setStatus(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @PathVariable Long bookingId,
                                @RequestParam Boolean approved) {
        log.info("Got request to PATCH booking with id {}", bookingId);
        return bookingService.setStatus(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        log.info("Got request to GET booking with id {}", bookingId);
        return bookingService.findById(userId, bookingId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> findAllByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Got request to GET all bookings with state {}", state);
        return bookingService.findAllByState(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllByItemOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(name = "state", defaultValue = "ALL") String state) {
        log.info("Got request to GET all bookings by owner id {}", userId);
        return bookingService.findAllByItemOwner(userId, state);
    }
}
