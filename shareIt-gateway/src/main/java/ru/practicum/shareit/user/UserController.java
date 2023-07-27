package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.PostRequestValidationGroup;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {

    @Autowired
    private UserClient userClient;

    @PostMapping
    @Validated(PostRequestValidationGroup.class)
    public ResponseEntity<Object> create(@RequestBody @Valid UserDto userDto) {
        log.info("Got request to POST user {}", userDto);
        return userClient.create(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable("userId") Long userId) {
        log.info("Got request to GET user with id {}", userId);
        return userClient.findById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAll(@RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                          @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Got request to GET all users from={}, size={}", from, size);
        return userClient.findAll(from, size);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable("userId") Long userId,
                                         @RequestBody @Valid UserDto userDto) {
        log.info("Got request to PATCH fields: {} to user with id {}", userDto, userId);
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") Long userId) {
        log.info("Got request to DELETE user with id {}", userId);
        return userClient.delete(userId);
    }


}
