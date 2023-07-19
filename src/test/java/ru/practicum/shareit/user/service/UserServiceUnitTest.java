package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceUnitTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    private Long userId;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        user = User.builder()
                .id(1L)
                .name("Name")
                .email("email@email.ru").build();
        userDto = UserDto.builder()
                //.id(1L)
                .name("Name")
                .email("email@email.ru").build();
    }

    @Test
    void findAll_whenInvoked_thenReturnUserDtoCollections() {
        Integer from = 1;
        Integer size = 1;
        PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size);
        List<User> usersFromRepository = List.of(user);
        List<UserDto> expectedUsersDto = List.of(userDto);
        when(userRepository.findAll(page)).thenReturn(new PageImpl<>(usersFromRepository));
        when(userMapper.userToDto(user)).thenReturn(userDto);

        List<UserDto> actualUsersDto = userService.findAll(from, size);

        assertEquals(expectedUsersDto, actualUsersDto);
    }

    @Test
    void findById_whenUserFound_thenReturnedUserDto() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        UserDto expectedUserDto = new UserDto();
        when(userMapper.userToDto(user))
                .thenReturn(expectedUserDto);

        UserDto actualUser = userService.findById(userId);

        assertEquals(expectedUserDto, actualUser);
    }

    @Test
    void findById_whenUserNotFound_thenEntityNotFoundExceptionThrow() {
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        EntityNotFoundException entityNotFoundException = assertThrows(EntityNotFoundException.class,
                () -> userService.findById(userId));

        assertEquals("User id not found in storage", entityNotFoundException.getMessage());
    }

    @Test
    void create_whenCreateUser_returnUserDto() {
        when(userMapper.userFromDto(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userToDto(user))
                .thenReturn(userDto);

        UserDto actualUserDto = userService.create(userDto);

        assertEquals(userDto, actualUserDto);
        Mockito.verify(userRepository).save(user);
    }

    @Test
    void delete_whenInvoke_thenInvokeUserRepository() {
        userService.delete(userId);

        Mockito.verify(userRepository).deleteById(0L);
    }

    @Test
    void update_whenUserFound_thenUpdatedOnlyAvailableFields() {
        UserDto newUserDto = UserDto.builder()
                .name("Update").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.update(userId, newUserDto);

        Mockito.verify(userMapper).userToDto(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();

        assertEquals(1L, savedUser.getId());
        assertEquals("Update", savedUser.getName());
        assertEquals("email@email.ru", savedUser.getEmail());
    }

    @Test
    void update_whenUserNotFound_thenEntityNotFoundExceptionThrow() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.findById(userId));
        Mockito.verify(userRepository, never()).saveAndFlush(any(User.class));
    }
}
