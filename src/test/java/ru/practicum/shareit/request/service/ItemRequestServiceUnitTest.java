package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Pagination;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRequestMapper itemRequestMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Captor
    private ArgumentCaptor<ItemRequest> argumentCaptorItemRequest;

    private Long userId;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

    public ItemRequestServiceUnitTest() {
    }

    @BeforeEach
    void beforeEach() {
        userId = 0L;
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("description")
                .build();
        itemRequest = ItemRequest.builder().build();
    }

    @Test
    void create_whenItemRequestAndUserExist_thenReturnItemRequestDto() {
        UserDto userDto = UserDto.builder().build();
        User user = User.builder().build();
        ItemRequest itemRequest = ItemRequest.builder()
                .created(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Europe/Moscow")))
                .build();
        ItemRequest expectedItemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(user)
                .created(LocalDateTime.ofInstant(Instant.now(), ZoneId.of("Europe/Moscow")))
                .build();

        when(userService.findById(userId)).thenReturn(userDto);
        when(userMapper.userFromDto(userDto)).thenReturn(user);
        when(itemRequestMapper.itemRequestFromDto(itemRequestDto)).thenReturn(itemRequest);
        when(itemRequestRepository.save(itemRequest)).thenReturn(expectedItemRequest);
        when(itemRequestMapper.itemRequestToDto(expectedItemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto actualItemRequestDto = itemRequestService.create(userId, itemRequestDto);

        assertEquals(1L, actualItemRequestDto.getId());
        assertEquals("description", actualItemRequestDto.getDescription());
        assertNull(actualItemRequestDto.getItems());

        Mockito.verify(itemRequestRepository).save(argumentCaptorItemRequest.capture());
        ItemRequest savedRequest = argumentCaptorItemRequest.getValue();

        assertEquals(user, savedRequest.getRequestor());
    }

    @Test
    void create_whenCreateItemRequestAndUserNotExist_thenEntityNotFoundExceptionThrow() {
        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.create(userId, itemRequestDto));
        Mockito.verify(itemRequestRepository, never()).save(any());
    }

    @Test
    void findAllRequestByUser_whenInvoke_thenReturnListItemRequestDto() {
        List<ItemRequest> requestsList = List.of(itemRequest);
        when(itemRequestRepository.findByRequestorIdOrderByCreatedAsc(userId)).thenReturn(requestsList);
        when(itemRequestMapper.itemRequestToDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> actualRequestsList = itemRequestService.getAllRequestByUser(userId);

        assertFalse(actualRequestsList.isEmpty());
        assertEquals(itemRequestDto, actualRequestsList.get(0));
    }

    @Test
    void getRequestById_whenItemRequestFound_thenReturnItemRequestDto() {
        Long requestId = 0L;
        when(itemRequestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.itemRequestToDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto actualRequestDto = itemRequestService.getRequestById(userId, requestId);

        assertEquals(itemRequestDto, actualRequestDto);
    }

    @Test
    void getRequestById_whenItemRequestNotFound_thenEntityNotFoundExceptionThrow() {
        Long requestId = 0L;
        when(itemRequestRepository.findById(requestId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
    }

    @Test
    void getAllRequests_whenInvoke_thenReturnCollectionItemRequestDto() {
        int from = 1;
        int size = 5;
        Pagination page = new Pagination(from, size);
        List<ItemRequest> requests = List.of(itemRequest);
        when(itemRequestRepository.findAllExceptRequestorIdOrderByCreatedAsc(userId, page)).thenReturn(requests);
        when(itemRequestMapper.itemRequestToDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> actualRequests = itemRequestService.getAllRequests(userId, from, size);

        assertFalse(actualRequests.isEmpty());
        assertEquals(itemRequestDto, actualRequests.get(0));
    }
}
