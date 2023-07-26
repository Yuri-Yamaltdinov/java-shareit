package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInitial;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.USERID_HEADER;

@WebMvcTest(BookingController.class)
public class BookingControllerIntegrationTest {
    private static Long userId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private BookingDtoInitial bookingRequestDto;
    private BookingDto bookingResponseDto;

    @BeforeAll
    static void beforeAll() {
        userId = 0L;
    }

    @BeforeEach
    void beforeEach() {
        bookingRequestDto = BookingDtoInitial.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L)).build();
        bookingResponseDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1L))
                .end(LocalDateTime.now().plusHours(2L)).build();
    }

    @Test
    void createWhenInvokeThenReturnStatusOk() throws Exception {
        when(bookingService.create(userId, bookingRequestDto)).thenReturn(bookingResponseDto);

        String result = mockMvc.perform(post("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), result);
    }

    @Test
    void createWhenUserNotFoundThenReturnStatusNotFound() throws Exception {
        when(bookingService.create(userId, bookingRequestDto)).thenThrow(new EntityNotFoundException(Booking.class, "Entity not found"));

        mockMvc.perform(post("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void setStatusWhenInvokeThenReturnStatusOkAndBookingResponseDtoInBody() {
        Long bookingId = 0L;
        Boolean approved = true;
        when(bookingService.setStatus(userId, bookingId, approved)).thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString())
                        .param("approved", "true"))
                .andExpect(status().isOk());
    }

    @SneakyThrows
    @Test
    void setStatusWhenDataNotFoundThenReturnStatusNotFound() {
        Long bookingId = 0L;
        Boolean approved = true;
        when(bookingService.setStatus(userId, bookingId, approved))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString())
                        .param("approved", "true"))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void setStatusWhenNoApprovedParamThenReturnStatusBadRequest() {
        Long bookingId = 0L;
        Boolean approved = true;
        when(bookingService.setStatus(userId, bookingId, approved))
                .thenThrow(ValidationException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isInternalServerError());
    }

    @SneakyThrows
    @Test
    void setStatusWhenNotUserOwnerThenReturnStatusBadRequest() {
        Long bookingId = 0L;
        Boolean approved = true;
        when(bookingService.setStatus(userId, bookingId, approved))
                .thenThrow(ValidationException.class);

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .param("approved", "true"))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findByIdWhenInvokeThenReturnStatusOK() {
        Long bookingId = 0L;
        BookingDto responseDto = BookingDto.builder().build();
        when(bookingService.findById(userId, bookingId)).thenReturn(responseDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseDto), result);

    }

    @SneakyThrows
    @Test
    void findByIdWhenDataNotFoundThenReturnStatusNotFound() {
        Long bookingId = 0L;
        when(bookingService.findById(userId, bookingId))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void findByIdWhenDuplicateBookingStatusThenReturnStatusBadRequest() {
        Long bookingId = 0L;
        when(bookingService.findById(userId, bookingId))
                .thenThrow(new ValidationException("exception message"));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllByStateWhenInvokeThenReturnStatusOK() {
        String state = "ALL";
        Integer from = 1;
        Integer size = 1;
        List<BookingDto> responseDtoList = Collections.emptyList();
        when(bookingService.findAllByState(userId, state, from, size))
                .thenReturn(responseDtoList);

        String result = mockMvc.perform(get("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseDtoList), result);
    }

    @SneakyThrows
    @Test
    void findAllByStateWhenNotValidStateThenReturnStatusBadRequest() {
        String state = "NotValid";
        int from = 1;
        int size = 1;
        when(bookingService.findAllByState(any(), any(), any(), any()))
                .thenThrow(new ValidationException("exception message"));

        mockMvc.perform(get("/bookings")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", Integer.toString(from))
                        .param("size", Integer.toString(size)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void findAllByItemOwnerWhenInvokeThenReturnStatusOK() {
        String state = "ALL";
        Integer from = 1;
        Integer size = 1;
        List<BookingDto> responseDtoList = Collections.emptyList();
        when(bookingService.findAllByItemOwner(userId, state, from, size))
                .thenReturn(responseDtoList);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header(USERID_HEADER, userId.toString())
                        .param("state", state)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(responseDtoList), result);
    }

}
