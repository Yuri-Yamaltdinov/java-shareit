package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getId() != null) {
            throw new ValidationException("User id should not exist in POST request");
        }

        User user = UserMapper.fromUserDto(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, "User id not found in storage"));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, "User id not found in storage"));

        user.setName(userDto.getName() != null ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail());
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
