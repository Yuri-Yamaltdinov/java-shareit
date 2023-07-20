package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceIntegrationTest {
    private final EntityManager entityManager;
    private final ItemRequestServiceImpl itemRequestService;
    private final UserService userService;
    private Long userId;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void beforeEach() {
        UserDto userDto = UserDto.builder()
                .name("User")
                .email("user@email.ru").build();
        userId = userService.create(userDto).getId();

        itemRequestDto = ItemRequestDto.builder().description("description").build();
    }

    @SneakyThrows
    @Test
    void create() {
        Long itemRequestId = itemRequestService.create(userId, itemRequestDto).getId();
        TypedQuery<ItemRequest> query = entityManager.createQuery("Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest itemRequestSaved = query.setParameter("id", itemRequestId).getSingleResult();

        assertNotNull(itemRequestSaved.getId());
        assertEquals(itemRequestSaved.getDescription(), itemRequestDto.getDescription());
        assertNotNull(itemRequestSaved.getRequestor());
        assertNotNull(itemRequestSaved.getCreated());
    }

    @Test
    void getAllRequestByUser() {
        List<ItemRequestDto> sourceRequests = new ArrayList<>(List.of(
                ItemRequestDto.builder().description("desc1").build(),
                ItemRequestDto.builder().description("desc2").build(),
                ItemRequestDto.builder().description("desc3").build()));
        UserDto userDtoNew = UserDto.builder()
                .name("UserNew")
                .email("userNew@email.ru").build();
        userId = userService.create(userDtoNew).getId();
        for (ItemRequestDto requestDto : sourceRequests) {
            itemRequestService.create(userId, requestDto);
        }

        List<ItemRequestDto> targetRequests = itemRequestService.getAllRequestByUser(userId);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDto sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", notNullValue())
            )));
        }
    }

    @Test
    void getRequestById() {
        Long itemRequestId = itemRequestService.create(userId, itemRequestDto).getId();

        ItemRequestDto targetItemRequest = itemRequestService.getRequestById(userId, itemRequestId);

        assertNotNull(targetItemRequest.getId());
        assertEquals(targetItemRequest.getDescription(), itemRequestDto.getDescription());
        assertNotNull(targetItemRequest.getCreated());
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDto> sourceRequests = new ArrayList<>(List.of(
                ItemRequestDto.builder().description("desc1").build(),
                ItemRequestDto.builder().description("desc2").build(),
                ItemRequestDto.builder().description("desc3").build()));
        UserDto userDtoNew = UserDto.builder()
                .name("NewUser")
                .email("newuser@email.ru").build();
        Long userId2 = userService.create(userDtoNew).getId();
        for (ItemRequestDto requestDto : sourceRequests) {
            itemRequestService.create(userId2, requestDto);
        }

        List<ItemRequestDto> targetRequests = itemRequestService.getAllRequests(userId, 0, 5);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDto sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", notNullValue())
            )));
        }
    }

    @Test
    void getAllRequests_withPaging() {
        List<ItemRequestDto> sourceRequests = new ArrayList<>(List.of(
                ItemRequestDto.builder().description("desc1").build(),
                ItemRequestDto.builder().description("desc2").build(),
                ItemRequestDto.builder().description("desc3").build()));
        UserDto userDtoNew = UserDto.builder()
                .name("NewUser")
                .email("newuser@email.ru").build();
        Long userId2 = userService.create(userDtoNew).getId();
        for (ItemRequestDto requestDto : sourceRequests) {
            itemRequestService.create(userId2, requestDto);
        }
        int from = 0;
        int size = 2;

        List<ItemRequestDto> targetRequests = itemRequestService.getAllRequests(userId, from, size);

        assertThat(targetRequests, hasSize(size));

        from = 2;
        targetRequests = itemRequestService.getAllRequests(userId2, from, size);

        assertThat(targetRequests, hasSize(0));
    }
}
