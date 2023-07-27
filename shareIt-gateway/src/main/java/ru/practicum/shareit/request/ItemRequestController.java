package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.PostRequestValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.item.ItemController.USERID_HEADER;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    @Validated(PostRequestValidationGroup.class)
    public ResponseEntity<Object> create(@RequestHeader(USERID_HEADER) Long userId,
                                         @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Got request to POST item request {}", itemRequestDto);
        return itemRequestClient.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUser(@RequestHeader(USERID_HEADER) Long userId) {
        log.info("Got request to GET item requests for user with id {}", userId);
        return itemRequestClient.getAllRequestByUser(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USERID_HEADER) Long userId,
                                                 @PathVariable Long requestId) {
        log.info("Got request to GET item request with id {}", requestId);
        return itemRequestClient.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USERID_HEADER) Long userId,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                 @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Got request to GET all item requests with userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAllRequests(userId, from, size);
    }
}
