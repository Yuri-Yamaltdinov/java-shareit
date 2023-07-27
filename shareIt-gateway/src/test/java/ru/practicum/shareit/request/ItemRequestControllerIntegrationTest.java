package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
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
    private ItemRequestClient itemRequestClient;
    private Long requestId;
    private Long userId;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        requestId = 0L;
        userId = 1L;
        itemRequestDto = ItemRequestDto.builder().build();
    }

    @Test
    void createWhenInvokeThenStatus2xx() throws Exception {
        itemRequestDto.setDescription("Description");

        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header(USERID_HEADER, userId.toString())
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().is2xxSuccessful());

        verify(itemRequestClient).create(userId, itemRequestDto);
    }

    @Test
    void createWhenNotValidDescriptionThenStatusBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header(USERID_HEADER, userId.toString())
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @Test
    void createWhenEmptyBodyThenStatusBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @Test
    void createWhenEmptyUserIdThenStatusBadRequest() throws Exception {
        mockMvc.perform(post("/requests")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).create(any(), any());
    }

    @Test
    void getAllRequestsByUserWhenInvokeThenStatusOK() throws Exception {
        mockMvc.perform(get("/requests")
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequestByUser(userId);
    }

    @Test
    void getAllRequestsByUserWhenNotUserIdThenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/requests"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequestByUser(any());
    }

    @Test
    void getAllRequestsByUserWhenServerNotFoundUserThenStatusNotFound() throws Exception {
        when(itemRequestClient.getAllRequestByUser(any()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/requests")
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRequestByIdWhenInvokeThenStatusOK() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk());

        verify(itemRequestClient).getRequestById(userId, requestId);
    }

    @Test
    void getRequestByIdWhenNotUserIdThenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/requests/{requestId}", requestId))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getRequestById(any(), any());
    }

    @Test
    void getRequestByIdWhenServerNotFoundResponseStatusThenStatusNotFound() throws Exception {
        when(itemRequestClient.getRequestById(any(), any()))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllRequestsWhenInvokeThenStatusOK() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(userId, 1, 1);
    }

    @Test
    void getAllRequestsWhenNotValidParamFromThenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", "-1")
                        .param("size", "1"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(any(), any(), any());
    }

    @Test
    void getAllRequestsWhenNotValidParamSize0ThenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", "1")
                        .param("size", "0"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(any(), any(), any());
    }

    @Test
    void getAllRequestsWhenNotValidParamSizeNegativeThenStatusBadRequest() throws Exception {
        mockMvc.perform(get("/requests/all")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", "1")
                        .param("size", "-1"))
                .andExpect(status().isBadRequest());

        verify(itemRequestClient, never()).getAllRequests(any(), any(), any());
    }

    @Test
    void getAllRequestsWithoutParamsThenStatusOk() throws Exception {
        int defaultFrom = 0;
        int defaultSize = 10;
        mockMvc.perform(get("/requests/all")
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk());

        verify(itemRequestClient).getAllRequests(userId, defaultFrom, defaultSize);
    }

}