package ru.practicum.shareit.user.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class UserMapper {
    public UserDto userToDto(User user) {
        if (user == null) {
            throw new ValidationException("User entity is null");
        }
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User userFromDto(UserDto userDto) {
        if (userDto == null) {
            throw new ValidationException("UserDto entity is null");
        }
        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }

    public UserBookingDto userToUserBookingDto(User user) {
        return UserBookingDto.builder()
                .id(user.getId())
                .build();
    }

}
