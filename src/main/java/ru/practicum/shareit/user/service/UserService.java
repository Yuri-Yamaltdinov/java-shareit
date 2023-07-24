package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto findById(Long id);

    List<UserDto> findAll(Integer from, Integer size);

    UserDto update(Long userId, UserDto userDto);

    void delete(Long id);

}
