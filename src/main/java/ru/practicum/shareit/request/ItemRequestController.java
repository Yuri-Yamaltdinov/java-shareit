package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.PostRequestValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

import static ru.practicum.shareit.item.ItemController.USERID_HEADER;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Validated(PostRequestValidationGroup.class)
    public ItemRequestDto create(@RequestHeader(USERID_HEADER) Long userId,
                                 @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("Got request to POST item request {}", itemRequestDto);
        return itemRequestService.create(userId, itemRequestDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllRequestsByUser(@RequestHeader(USERID_HEADER) Long userId) {
        log.info("Got request to GET item requests for user with id {}", userId);
        return itemRequestService.getAllRequestByUser(userId);
    }

    @GetMapping("/{requestId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemRequestDto getRequestById(@RequestHeader(USERID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        log.info("Got request to GET item request with id {}", requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USERID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                               @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Got request to GET all item requests");
        return itemRequestService.getAllRequests(userId, from, size);
    }


}
