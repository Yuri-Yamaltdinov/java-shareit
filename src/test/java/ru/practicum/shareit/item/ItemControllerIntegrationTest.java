package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerIntegrationTest {
    private static Long userId;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;

    @BeforeAll
    static void beforeAll() {
        userId = 0L;
    }

    @SneakyThrows
    @Test
    void create_whenInvoke_thenStatusCreateItemDtoInBody() {
        ItemDto itemDto = ItemDto.builder()
                .name("Name")
                .description("desc")
                .available(true).build();
        when(itemService.create(userId, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
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
    void create_whenBodyNotValid_thenStatusBadRequest() {
        ItemDto itemDto = ItemDto.builder()
                .description("desc")
                .available(true).build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(userId, itemDto);
    }

    @SneakyThrows
    @Test
    void create_whenNotHeadUserId_thenStatusBadRequest() {
        ItemDto itemDto = ItemDto.builder()
                .description("desc")
                .available(true).build();

        mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).create(userId, itemDto);
    }

    @SneakyThrows
    @Test
    void create_whenUserNotFound_thenthenStatusNotFound() {
        Long wrongUserId = 100L;
        ItemDto itemDto = ItemDto.builder()
                .description("desc")
                .available(true).build();

        ResultActions resultActions = mockMvc.perform(post("/items")
                .header("X-Sharer-User-Id", wrongUserId.toString())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(itemDto)));

        resultActions.andExpect(status().isBadRequest());
        String body = resultActions.andReturn().getResponse().getContentAsString();

        verify(itemService, never()).create(wrongUserId, itemDto);
    }

    @SneakyThrows
    @Test
    void update_whenInvoke_thenStatusOkItemDtoInBody() {
        Long itemId = 0L;
        ItemDto itemDto = ItemDto.builder()
                .description("desc")
                .available(true).build();
        when(itemService.update(userId, itemId, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString())
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
    void update_whenItemNotFound_thenStatusNotFound() {
        Long itemId = 0L;
        ItemDto itemDto = ItemDto.builder()
                .description("desc")
                .available(true).build();
        when(itemService.update(userId, itemId, itemDto))
                .thenThrow(AccessException.class);

        mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isForbidden());
    }

    @SneakyThrows
    @Test
    void update_whenUserNotOwner_thenStatusNotFound() {
        Long itemId = 0L;
        userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .description("desc")
                .available(true).build();
        when(itemService.update(userId, itemId, itemDto))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void update_whenUserNotFound_thenStatusNotFound() {
        Long itemId = 0L;
        userId = 2L;
        ItemDto itemDto = ItemDto.builder()
                .description("desc")
                .available(true).build();
        when(itemService.update(anyLong(), anyLong(), any()))
                .thenThrow(EntityNotFoundException.class);
        mockMvc.perform(patch("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getByItemId_whenInvoke_thenStatusOkItemBookedInBody() {
        Long itemId = 0L;
        ItemDtoWithBookingsAndComments itemBooked = ItemDtoWithBookingsAndComments.builder()
                .description("desc")
                .available(true).build();
        when(itemService.findById(userId, itemId)).thenReturn(itemBooked);

        String result = mockMvc.perform(get("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemBooked), result);
    }

    @SneakyThrows
    @Test
    void getByItemId_whenItemNotFound_thenStatusNotFound() {
        Long itemId = 0L;
        when(itemService.findById(userId, itemId))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(get("/items/{itemId}", itemId.toString())
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void getAllItemsByUserId_whenInvoke_thenStatusOkItemBookedListInBody() {
        Integer from = 1;
        Integer size = 1;
        List<ItemDtoWithBookingsAndComments> itemBookedList = List.of(ItemDtoWithBookingsAndComments.builder()
                .description("desc")
                .available(true).build());
        when(itemService.findAll(userId, from, size)).thenReturn(itemBookedList);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId.toString())
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
    void getAllItemsByUserId_whenParamsNotValid_thenStatusBadRequest() {
        Integer from = -1;
        Integer size = -1;

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findAll(userId, from, size);
    }

    @SneakyThrows
    @Test
    void delete_whenInvoke_thenStatusNoContent() {
        Long itemId = 0L;

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId.toString()))
                .andExpect(status().isNoContent());

        verify(itemService, times(1)).delete(userId, itemId);
    }

    @SneakyThrows
    @Test
    void search_whenInvoke_thenStatusOk() {
        String text = "text";
        Integer from = 1;
        Integer size = 1;
        List<ItemDto> itemDtoList = List.of(ItemDto.builder().build());
        when(itemService.search(userId, text, from, size)).thenReturn(itemDtoList);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
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
    void search_whenParamsNotValid_thenStatusBadRequest() {
        String text = "text";
        Integer from = -1;
        Integer size = -1;

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId.toString())
                        .param("text", text)
                        .param("from", from.toString())
                        .param("size", size.toString()))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).findAll(userId, from, size);
    }

    @SneakyThrows
    @Test
    void createComment_whenInvoke_thenStatusOk() {
        Long itemId = 0L;
        CommentDto commentDto = CommentDto.builder().text("test").build();
        when(itemService.createComment(userId, itemId, commentDto)).thenReturn(commentDto);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
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
    void createComment_whenItemNotFound_thenStatusNotFound() {
        Long itemId = 0L;
        CommentDto commentDto = CommentDto.builder().text("test").build();
        when(itemService.createComment(userId, itemId, commentDto))
                .thenThrow(EntityNotFoundException.class);

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isNotFound());
    }

    @SneakyThrows
    @Test
    void createComment_whenBookingNotFound_thenStatusBadRequest() {
        Long itemId = 0L;
        CommentDto commentDto = CommentDto.builder().text("test").build();
        when(itemService.createComment(userId, itemId, commentDto))
                .thenThrow(new ValidationException("Exception message"));

        mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", userId.toString())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }
}
