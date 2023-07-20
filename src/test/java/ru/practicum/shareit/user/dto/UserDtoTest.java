package ru.practicum.shareit.user.dto;

import lombok.SneakyThrows;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @SneakyThrows
    @Test
    void testUserCreateDto() {
        UserDto userCreateDto = UserDto.builder()
                .name("name")
                .email("email@email.ru")
                .build();

        JsonContent<UserDto> content = jacksonTester.write(userCreateDto);

        Assertions.assertThat(content).extractingJsonPathStringValue("$.name")
                .isEqualTo(userCreateDto.getName());
        Assertions.assertThat(content).extractingJsonPathStringValue("$.email")
                .isEqualTo(userCreateDto.getEmail());
    }

    @SneakyThrows
    @Test
    void testUserDto() {
        UserDto userDto = UserDto.builder()
                .id(0L)
                .name("name")
                .email("email@email.ru")
                .build();

        JsonContent<UserDto> content = jacksonTester.write(userDto);

        assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(userDto.getId().intValue());
        assertThat(content).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(content).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }
}
