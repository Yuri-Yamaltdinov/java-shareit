package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerIntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserClient userClient;
    private Long userId;
    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        userDto = UserDto.builder().build();
    }

    @Test
    void findAllWhenInvokeThenStatusOK() throws Exception {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());

        verify(userClient).findAll(1, 1);
    }

    @Test
    void findAllWhenNotValidParamFromThenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .param("from", "-1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).findAll(any(), any());
    }

    @Test
    void findAllWhenNotValidParamSize0ThenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).findAll(any(), any());
    }

    @Test
    void findAllWhenNotValidParamSizeNegativeThenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/users")
                        .param("from", "1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).findAll(any(), any());
    }

    @Test
    void findAllWithoutParamsThenStatusOk() throws Exception {
        int defaultFrom = 0;
        int defaultSize = 10;
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userClient).findAll(defaultFrom, defaultSize);
    }

    @Test
    void findByIdWhenInvokeThenStatusOk() throws Exception {
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userClient).findById(userId);
    }

    @Test
    void findByIdWhenUserNotFoundThenStatusNotFound() throws Exception {
        when(userClient.findById(any()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isNotFound());

        verify(userClient).findById(userId);
    }

    @Test
    void createWhenInvokeThenStatus2xx() throws Exception {
        userDto = UserDto.builder()
                .name("Name")
                .email("user@mail.ru").build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is2xxSuccessful());

        verify(userClient).create(userDto);
    }

    @Test
    void createWhenNotValidUserNameThenStatusBadRequest() throws Exception {
        userDto = UserDto.builder()
                .email("user@mail.ru").build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(userDto);
    }

    @Test
    void createWhenNotValidUserEmailThenStatusBadRequest() throws Exception {
        userDto = UserDto.builder()
                .name("User")
                .email(" ").build();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(userDto);
    }

    @Test
    void createWhenEmptyBodyThenStatusBadRequest() throws Exception {
        mockMvc.perform(post("/users")
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).create(userDto);
    }

    @Test
    void deleteWhenInvokeThenStatus2xx() throws Exception {
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().is2xxSuccessful());

        verify(userClient).delete(userId);
    }

    @Test
    void updateWhenInvokeThenStatus2xx() throws Exception {
        userDto = UserDto.builder()
                .name("Name")
                .email("user@mail.ru").build();

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().is2xxSuccessful());

        verify(userClient).update(userId, userDto);
    }

    @Test
    void updateWhenUserNotFoundThenStatusNotFound() throws Exception {
        userDto = UserDto.builder()
                .name("Name")
                .email("user@mail.ru").build();
        when(userClient.update(userId, userDto))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isNotFound());

        verify(userClient).update(userId, userDto);
    }

    @Test
    void updateWhenEmptyBodyThenStatusBadRequest() throws Exception {
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).update(userId, userDto);
    }
}