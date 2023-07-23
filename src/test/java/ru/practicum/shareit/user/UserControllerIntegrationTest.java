package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    private Long userId;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        userDto = UserDto.builder()
                .name("Name")
                .email("user@email.ru").build();
    }

    @SneakyThrows
    @Test
    void createWhenInvokeThenInvokeUserService() {
        when(userService.create(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).create(userDto);
    }

    @SneakyThrows
    @Test
    void createWithNotValidParamsThenReturnBadRequest() {
        userDto.setName(null);
        userDto.setEmail(null);
        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).create(userDto);
    }

    @SneakyThrows
    @Test
    void createWhenDuplicateUserThenConflictStatus() {
        when(userService.create(userDto)).thenThrow(ConflictException.class);

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict());

        verify(userService).create(userDto);
    }

    @SneakyThrows
    @Test
    void findByIdWhenInvokeThenInvokeUserService() {
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).findById(userId);
    }

    @SneakyThrows
    @Test
    void findByIdWhenUserNotFoundThenStatusNotFound() {
        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findAllWithValidParams() {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());

        verify(userService).findAll(1, 1);
    }

    @SneakyThrows
    @Test
    void findAllWithNotValidParamsThenReturnBadRequest() {
        mockMvc.perform(get("/users")
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).findAll(1, 1);
    }

    @SneakyThrows
    @Test
    void findAllWithoutParams() {
        int defaultFrom = 0;
        int defaultSize = 10;
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService).findAll(defaultFrom, defaultSize);
    }

    @SneakyThrows
    @Test
    void deleteWhenInvokeThenNoContentStatus() {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isNoContent());
        verify(userService).delete(userId);
    }

    @SneakyThrows
    @Test
    void updateWhenInvokeThenStatusOK() {
        when(userService.update(userId, userDto)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);
        verify(userService).update(userId, userDto);
    }

    @SneakyThrows
    @Test
    void updateWhenUserNotFoundThenStatusNotFound() {
        when(userService.update(userId, userDto)).thenThrow(EntityNotFoundException.class);
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());
    }
}
