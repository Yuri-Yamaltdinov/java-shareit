package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
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
                             @RequestBody @Valid BookingDto bookingDto) {
        log.info("Got request to POST booking {}", bookingDto);
        return bookingService.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.OK)
    public BookingDto bookingConfirmation(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long bookingId,
                                          @RequestParam Boolean approved) {
        log.info("Got request to PATCH booking with id {}", bookingId);
        return bookingService.bookingConfirmation(userId, bookingId, approved);
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
                                           @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Got request to GET all bookings with state {}", state);
        return bookingService.findAllByState(userId, state);
    }

    @GetMapping("/owner")
    @ResponseStatus(HttpStatus.OK)
    public List<BookingDto> getAllByItemOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(defaultValue = "ALL", required = false) String state) {
        log.info("Got request to GET all bookings by owner id {}", userId);
        return bookingService.findAllByItemOwner(userId, state);
    }
}
