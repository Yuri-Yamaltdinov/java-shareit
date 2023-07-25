package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.USERID_HEADER;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerIntegrationTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemRequestService itemRequestService;
    private Long userId;
    private Long requestId;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        requestId = 0L;
        itemRequestDto = ItemRequestDto.builder().build();
    }

    @SneakyThrows
    @Test
    void createWhenInvokeThenInvokeItemRequestService() {
        itemRequestDto.setDescription("desc");
        when(itemRequestService.create(userId, itemRequestDto)).thenReturn(itemRequestDto);

        String result = mockMvc.perform(post("/requests")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void createWhenBodyNotValidThenStatusBadRequestBadRequest() {
        mockMvc.perform(post("/requests")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestService, never()).create(userId, itemRequestDto);
    }

    @SneakyThrows
    @Test
    void createWhenUserNotFoundThenStatusNotFound() {
        itemRequestDto.setDescription("desc");
        when(itemRequestService.create(userId, itemRequestDto))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/requests")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllRequestsByUserWhenInvokeThenStatusOkAndListRequestsInBody() {
        List<ItemRequestDto> itemRequestDtoList = List.of(ItemRequestDto.builder()
                .description("desc").build());
        when(itemRequestService.getAllRequestByUser(userId)).thenReturn(itemRequestDtoList);

        String result = mockMvc.perform(get("/requests")
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);
    }

    @SneakyThrows
    @Test
    void getRequestByIdWhenInvokeThenStatusOkAndItemRequestsInBody() {
        itemRequestDto.setDescription("desc");
        when(itemRequestService.getRequestById(userId, requestId)).thenReturn(itemRequestDto);

        String result = mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDto), result);
    }

    @SneakyThrows
    @Test
    void getRequestByIdWhenItemRequestNotFoundThenStatusNotFound() {
        when(itemRequestService.getRequestById(userId, requestId))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllRequestsWithValidParams() {
        List<ItemRequestDto> itemRequestDtoList = List.of(ItemRequestDto.builder()
                .description("desc").build());
        when(itemRequestService.getAllRequests(userId, 1, 1)).thenReturn(itemRequestDtoList);

        String result = mockMvc.perform(get("/requests/all")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);
    }

    @SneakyThrows
    @Test
    void getAllRequestsWithNotValidParamsThenReturnBadRequest() {
        mockMvc.perform(get("/requests/all")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", "-1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).getAllRequests(userId, 1, 1);
    }
}
