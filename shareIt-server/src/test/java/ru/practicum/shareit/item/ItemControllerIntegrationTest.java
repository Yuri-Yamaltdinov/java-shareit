package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.item.ItemController.USERID_HEADER;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerIntegrationTest {
    private static Long userId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    private ItemDto itemDto;
    private CommentDto commentDto;
    private Long itemId;

    @BeforeAll
    static void beforeAll() {
        userId = 0L;
    }

    @BeforeEach
    void beforeEach() {
        itemDto = ItemDto.builder()
                .description("desc")
                .available(true).build();
        itemId = 0L;
        commentDto = CommentDto.builder().text("test").build();
    }

    @SneakyThrows
    @Test
    void createWhenInvokeThenStatusCreateItemDtoInBody() {
        itemDto.setName("Name");
        when(itemService.create(userId, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void createWhenNotHeadUserIdThenStatusBadRequest() {
        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(userId, itemDto);
    }

    @SneakyThrows
    @Test
    void updateWhenInvokeThenStatusOkItemDtoInBody() {
        when(itemService.update(userId, itemId, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void updateWhenItemNotFoundThenStatusNotFound() {
        when(itemService.update(userId, itemId, itemDto))
                .thenThrow(AccessException.class);

        mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    void updateWhenUserNotOwnerThenStatusNotFound() {
        userId = 1L;
        when(itemService.update(userId, itemId, itemDto))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void updateWhenUserNotFoundThenStatusNotFound() {
        userId = 2L;
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(EntityNotFoundException.class);
        mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getByItemIdWhenInvokeThenStatusOkItemBookedInBody() {
        ItemDtoWithBookingsAndComments itemBooked = ItemDtoWithBookingsAndComments.builder()
                .description("desc")
                .available(true).build();
        when(itemService.findById(userId, itemId)).thenReturn(itemBooked);

        String result = mockMvc.perform(get("/items/{itemId}", itemId.toString())
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemBooked), result);
    }

    @SneakyThrows
    @Test
    void getByItemIdWhenItemNotFoundThenStatusNotFound() {
        when(itemService.findById(userId, itemId))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", itemId.toString())
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllItemsByUserIdWhenInvokeThenStatusOkItemBookedListInBody() {
        Integer from = 1;
        Integer size = 1;
        List<ItemDtoWithBookingsAndComments> itemBookedList = List.of(ItemDtoWithBookingsAndComments.builder()
                .description("desc")
                .available(true).build());
        when(itemService.findAll(userId, from, size)).thenReturn(itemBookedList);

        String result = mockMvc.perform(get("/items")
                        .header(USERID_HEADER, userId.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemBookedList), result);
    }

    @SneakyThrows
    @Test
    void deleteWhenInvokeThenStatusNoContent() {
        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header(USERID_HEADER, userId.toString()))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).delete(userId, itemId);
    }

    @SneakyThrows
    @Test
    void searchWhenInvokeThenStatusOk() {
        String text = "text";
        Integer from = 1;
        Integer size = 1;
        List<ItemDto> itemDtoList = List.of(ItemDto.builder().build());
        when(itemService.search(userId, text, from, size)).thenReturn(itemDtoList);

        String result = mockMvc.perform(get("/items/search")
                        .header(USERID_HEADER, userId.toString())
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoList), result);
    }

    @SneakyThrows
    @Test
    void createCommentWhenInvokeThenStatusOk() {
        when(itemService.createComment(userId, itemId, commentDto)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDto), result);
    }

    @SneakyThrows
    @Test
    void createCommentWhenItemNotFoundThenStatusNotFound() {
        when(itemService.createComment(userId, itemId, commentDto))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createCommentWhenBookingNotFoundThenStatusBadRequest() {
        when(itemService.createComment(userId, itemId, commentDto))
                .thenThrow(new ValidationException("Exception message"));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header(USERID_HEADER, userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}
