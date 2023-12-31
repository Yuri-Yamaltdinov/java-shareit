package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Pagination;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        if (userDto.getId() != null) {
            throw new ValidationException("User id should not exist in POST request");
        }
        try {
            User user = userMapper.userFromDto(userDto);
            return userMapper.userToDto(userRepository.save(user));
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("User with email already exists.");
        }
    }

    @Override
    public UserDto findById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, "User id not found in storage"));
        return userMapper.userToDto(user);
    }

    @Override
    public List<UserDto> findAll(Integer from, Integer size) {
        Pagination page = new Pagination(from, size);

        return userRepository.findAll(page)
                .stream()
                .map(userMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto update(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, "User id not found in storage"));

        user.setName(userDto.getName() != null ? userDto.getName() : user.getName());
        user.setEmail(userDto.getEmail() != null ? userDto.getEmail() : user.getEmail());
        return userMapper.userToDto(user);
    }

    @Override
    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
