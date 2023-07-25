package ru.practicum.shareit.user.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.dto.UserBookingDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);
    private User user;

    @BeforeEach
    void beforeEach() {
        user = User.builder()
                .id(1L)
                .email("name@name.ru")
                .name("Name").build();
    }

    @Test
    void userToDto() {
        UserDto userDto = mapper.userToDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(),
                userDto.getEmail());
    }

    @Test
    void userFromDto() {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("name@name.ru")
                .name("Name").build();

        User actualUser = mapper.userFromDto(userDto);

        assertEquals(1L, actualUser.getId());
        assertEquals("Name", actualUser.getName());
        assertEquals("name@name.ru", actualUser.getEmail());
    }

    @Test
    void userToUserBookingDto() {
        UserBookingDto userBookingDto = mapper.userToUserBookingDto(user);

        assertEquals(user.getId(), userBookingDto.getId());

    }


}
