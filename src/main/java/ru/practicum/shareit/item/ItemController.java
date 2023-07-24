package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.PostRequestValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemService itemService;
    public static final String USERID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    @Validated(PostRequestValidationGroup.class)
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@RequestHeader(USERID_HEADER) Long userId,
                          @RequestBody @Valid ItemDto itemDto) {
        log.info("Got request to POST item {}", itemDto);
        return itemService.create(userId, itemDto);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDtoWithBookingsAndComments> findAll(@RequestHeader(USERID_HEADER) Long userId,
                                                        @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                        @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Got request to GET all items by user id {}", userId);
        return itemService.findAll(userId, from, size);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDtoWithBookingsAndComments findById(@RequestHeader(USERID_HEADER) Long userId,
                                                   @PathVariable("itemId") Long itemId) {
        log.info("Got request to GET item by id {}", itemId);
        return itemService.findById(userId, itemId);
    }

    @PatchMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto update(@RequestHeader(USERID_HEADER) Long userId,
                          @PathVariable("itemId") Long itemId,
                          @RequestBody ItemDto itemDto) {
        log.info("Got request to PATCH item {}", itemDto);
        return itemService.update(userId, itemId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestHeader(USERID_HEADER) Long userId,
                       @PathVariable("itemId") Long itemId) {
        log.info("Got request to DELETE item id {} of user id {}", itemId, userId);
        itemService.delete(userId, itemId);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> search(@RequestHeader(USERID_HEADER) Long userId,
                                @RequestParam("text") String text,
                                @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                @RequestParam(defaultValue = "10") @Min(1) Integer size) {
        log.info("Got request to GET items with text {}", text);
        return itemService.search(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto createComment(@RequestHeader(USERID_HEADER) Long userId,
                                    @PathVariable("itemId") Long itemId,
                                    @RequestBody @Valid CommentDto commentDto) {
        log.info("Got request to POST comment {}", commentDto);
        return itemService.createComment(userId, itemId, commentDto);
    }
}
