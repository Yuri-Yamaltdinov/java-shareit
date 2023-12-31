package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Pagination;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Captor
    private ArgumentCaptor<Item> argumentCaptorItem;
    @Captor
    private ArgumentCaptor<Comment> argumentCaptorComment;

    private Long itemId;
    private Long userId;
    private UserDto userDto;
    private User user;
    private ItemDto itemDto;
    private Item item;
    private ItemDtoWithBookingsAndComments itemDtoWithBookingsAndComments;
    private BookingInfoDto booking;
    private List<Comment> comments;
    private Integer from;
    private Integer size;

    @BeforeEach
    void beforeEach() {
        itemId = 0L;
        userId = 0L;
        userDto = UserDto.builder().build();
        user = User.builder().id(0L).build();
        itemDto = ItemDto.builder().requestId(0L).build();
        item = Item.builder().build();
        from = 1;
        size = 1;
        itemDtoWithBookingsAndComments = ItemDtoWithBookingsAndComments.builder().build();
        comments = List.of(Comment.builder().build());
        booking = BookingInfoDto.builder().id(1L).build();
    }

    @Test
    void createWithUserAndRequestExistThenReturnItemDto() {
        when(userService.findById(userId)).thenReturn(userDto);
        when(userMapper.userFromDto(userDto)).thenReturn(user);

        Item itemSaved = Item.builder().build();
        ItemDto savedItemDto = ItemDto.builder().id(0L).name("saved item").build();
        when(itemMapper.itemFromDto(itemDto)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(itemSaved);
        when(itemMapper.itemToDto(itemSaved)).thenReturn(savedItemDto);

        ItemRequest request = ItemRequest.builder().id(0L).build();
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.ofNullable(request));

        ItemDto actualItemDto = itemService.create(userId, itemDto);

        assertEquals(0L, actualItemDto.getId());
        assertEquals("saved item", actualItemDto.getName());

        Mockito.verify(itemRepository).save(argumentCaptorItem.capture());

        Item savedItem = argumentCaptorItem.getValue();

        assertNotNull(savedItem.getOwner());
        assertEquals(0L, savedItem.getOwner().getId());
        assertNotNull(savedItem.getRequest());
        assertEquals(0L, savedItem.getRequest().getId());
    }

    @Test
    void createWithUserNotExistThenEntityNotFoundExceptionThrow() {
        doThrow(EntityNotFoundException.class).when(userService).findById(userId);

        assertThrows(EntityNotFoundException.class,
                () -> itemService.create(userId, itemDto));
        Mockito.verify(itemRepository, never()).save(Mockito.any());
    }

    @Test
    void createWithUserExistRequestNotExistThenEntityNotFoundExceptionThrow() {
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.create(userId, itemDto));
        Mockito.verify(itemRepository, never()).save(Mockito.any());
    }

    @Test
    void updateWhenItemExistAndUserIsOwnerThenUpdateAndReturnItemDto() {
        ItemDto itemDtoUpdated = ItemDto.builder().name("Updated item").build();
        Item itemOld = Item.builder()
                .id(0L)
                .name("Old item")
                .owner(User.builder().id(0L).build())
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemOld));
        itemService.update(userId, itemId, itemDtoUpdated);
        Mockito.verify(itemMapper).itemToDto(argumentCaptorItem.capture());

        Item savedItem = argumentCaptorItem.getValue();

        assertEquals(0L, savedItem.getId());
        assertEquals("Updated item", savedItem.getName());
    }

    @Test
    void updateWhenItemNotExistThenEntityNotFoundExceptionThrow() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.update(userId, itemId, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void updateWhenUserNotOwnerThenDataAccessExceptionThrow() {
        Item itemOld = Item.builder()
                .owner(User.builder().id(1L).build())
                .build();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemOld));

        assertThrows(AccessException.class,
                () -> itemService.update(userId, itemId, itemDto));

        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void findByIdWhenInvokeThenReturnItemDtoWithBookingsAndComments() {
        item.setOwner(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.itemToItemDtoWithBookingAndComments(item)).thenReturn(itemDtoWithBookingsAndComments);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(any(), any(), any()))
                .thenReturn(Optional.of(Booking.builder().build()));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(any(), any(), any()))
                .thenReturn(Optional.of(Booking.builder().build()));
        when(bookingMapper.bookingToInfoDto(any())).thenReturn(booking);
        when(commentRepository.findByItemIdOrderByCreatedDesc(any())).thenReturn(comments);
        when(commentMapper.commentToDto(any())).thenReturn(CommentDto.builder().build());

        ItemDtoWithBookingsAndComments actualItemBooked = itemService.findById(userId, itemId);

        assertNotNull(actualItemBooked);
        assertNotNull(actualItemBooked.getLastBooking());
        assertNotNull(actualItemBooked.getNextBooking());
        assertNotNull(actualItemBooked.getComments());
        assertFalse(actualItemBooked.getComments().isEmpty());
    }

    @Test
    void findByIdWhenItemNotFoundThenEntityNotFoundExceptionThrow() {
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.findById(userId, itemId));
    }

    @Test
    void findByIdWhenUserNotOwnerThenReturnItemBookedWithNullBookings() {
        user.setId(1L);
        item.setOwner(user);
        ItemDtoWithBookingsAndComments itemDtoWithBookingsAndComments = ItemDtoWithBookingsAndComments.builder().build();
        List<Comment> comments = List.of(Comment.builder().build());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.itemToItemDtoWithBookingAndComments(item)).thenReturn(itemDtoWithBookingsAndComments);
        when(commentRepository.findByItemIdOrderByCreatedDesc(any())).thenReturn(comments);
        when(commentMapper.commentToDto(any())).thenReturn(CommentDto.builder().build());

        ItemDtoWithBookingsAndComments actualItemBooked = itemService.findById(userId, itemId);

        assertNotNull(actualItemBooked);
        assertNull(actualItemBooked.getLastBooking());
        assertNull(actualItemBooked.getNextBooking());
        assertNotNull(actualItemBooked.getComments());
        assertFalse(actualItemBooked.getComments().isEmpty());
    }

    @Test
    void getByItemIdWhenCommentsNotFoundThenReturnItemBookedWithEmptyListComments() {
        item.setOwner(user);
        ItemDtoWithBookingsAndComments itemDtoWithBookingsAndComments = ItemDtoWithBookingsAndComments.builder().build();
        BookingInfoDto booking = BookingInfoDto.builder().id(1L).build();
        List<Comment> comments = Collections.emptyList();
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.itemToItemDtoWithBookingAndComments(item)).thenReturn(itemDtoWithBookingsAndComments);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(any(), any(), any()))
                .thenReturn(Optional.of(Booking.builder().build()));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(any(), any(), any()))
                .thenReturn(Optional.of(Booking.builder().build()));
        when(bookingMapper.bookingToInfoDto(any())).thenReturn(booking);
        when(commentRepository.findByItemIdOrderByCreatedDesc(any())).thenReturn(comments);

        ItemDtoWithBookingsAndComments actualItemBooked = itemService.findById(userId, itemId);

        assertNotNull(actualItemBooked);
        assertNotNull(actualItemBooked.getLastBooking());
        assertNotNull(actualItemBooked.getNextBooking());
        assertNotNull(actualItemBooked.getComments());
        assertTrue(actualItemBooked.getComments().isEmpty());
    }

    @Test
    void findAllWhenInvokeThenReturnListItemBooked() {
        Pagination page = new Pagination(from, size);
        List<Item> items = List.of(item);
        Page<Item> itemsPage = new PageImpl<>(items, page, size);
        ItemDtoWithBookingsAndComments itemDtoWithBookingsAndComments = ItemDtoWithBookingsAndComments.builder().build();
        BookingInfoDto booking = BookingInfoDto.builder().id(1L).build();
        List<Comment> comments = List.of(Comment.builder().build());
        when(itemRepository.findAllByUserId(userId, page)).thenReturn(itemsPage);
        when(itemMapper.itemToItemDtoWithBookingAndComments(item)).thenReturn(itemDtoWithBookingsAndComments);
        when(bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(any(), any(), any()))
                .thenReturn(Optional.of(Booking.builder().build()));
        when(bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(any(), any(), any()))
                .thenReturn(Optional.of(Booking.builder().build()));
        when(bookingMapper.bookingToInfoDto(any())).thenReturn(booking);
        when(commentRepository.findByItem(any())).thenReturn(comments);
        when(commentMapper.commentToDto(any())).thenReturn(CommentDto.builder().build());

        List<ItemDtoWithBookingsAndComments> actualItems = itemService.findAll(userId, from, size);

        assertNotNull(actualItems);
        assertFalse(actualItems.isEmpty());

        ItemDtoWithBookingsAndComments actualItem = actualItems.get(0);

        assertNotNull(actualItem.getLastBooking());
        assertNotNull(actualItem.getNextBooking());
        assertNotNull(actualItem.getComments());
        assertFalse(actualItem.getComments().isEmpty());
    }

    @Test
    void findAllWhenItemNotFoundThenReturnEmptyListItemBooked() {
        Pagination page = new Pagination(from, size);
        when(itemRepository.findAllByUserId(userId, page)).thenReturn(Page.empty());

        List<ItemDtoWithBookingsAndComments> actualItems = itemService.findAll(userId, from, size);

        assertNotNull(actualItems);
        assertTrue(actualItems.isEmpty());
    }

    @Test
    void deleteWithItemExistAndUserIsOwnerThenInvokeItemRepositoryDeleteMethod() {
        item.setOwner(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.delete(userId, itemId);

        verify(itemRepository, times(1)).deleteById(itemId);
    }

    @Test
    void deleteWithItemExistAndUserNotOwnerThenDataAccessExceptionThrow() {
        user.setId(1L);
        item.setOwner(user);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(AccessException.class,
                () -> itemService.delete(userId, itemId));

        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void searchWhenInvokeThenReturnCollectionItemDto() {
        String text = "text";
        List<Item> items = List.of(item);
        Pagination page = new Pagination(from, size);
        Page<Item> itemsPage = new PageImpl<>(items, page, size);
        when(itemRepository.search(text, page)).thenReturn(itemsPage);
        when(itemMapper.itemToDto(item)).thenReturn(itemDto);

        List<ItemDto> actualItems = itemService.search(userId, text, from, size);

        assertFalse(actualItems.isEmpty());
        assertEquals(itemDto, actualItems.get(0));
    }

    @Test
    void searchWithEmptyTextThenReturnCollectionItemDto() {
        String text = "";
        List<ItemDto> actualItems = itemService.search(userId, text, from, size);

        assertTrue(actualItems.isEmpty());
    }

    @Test
    void searchWhenNotFoundItemsThenEntityNotFoundExceptionThrows() {
        String text = "text";
        Pagination page = new Pagination(from, size);
        when(itemRepository.search(text, page)).thenReturn(Page.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.search(userId, text, from, size));
    }

    @Test
    void createCommentWhenInvokeThenReturnCommentDto() {
        CommentDto commentDto = CommentDto.builder().text("CommentDto").build();
        Comment comment = Comment.builder().id(0L).build();
        Comment savedComment = Comment.builder().build();
        CommentDto savedDto = CommentDto.builder().text("saved").build();
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userMapper.userFromDto(userDto)).thenReturn(user);
        when(bookingRepository
                .findFirstByItemIdAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Optional.of(Booking.builder().build()));
        when(commentMapper.commentFromDto(commentDto)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(savedComment);
        when(commentMapper.commentToDto(savedComment)).thenReturn(savedDto);

        CommentDto actualDto = itemService.createComment(userId, itemId, commentDto);

        assertEquals("saved", actualDto.getText());

        verify(commentRepository).save(argumentCaptorComment.capture());

        Comment commentSendToSave = argumentCaptorComment.getValue();

        assertEquals(0L, commentSendToSave.getId());
        assertNotNull(commentSendToSave.getItem());
        assertNotNull(commentSendToSave.getAuthor());
    }

    @Test
    void createCommentWhenUserNotExistThenEntityNotFoundExceptionThrow() {
        CommentDto commentDto = CommentDto.builder().text("Comment").build();
        when(userService.findById(userId)).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class,
                () -> itemService.createComment(userId, itemId, commentDto));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentWhenItemNotExistThenEntityNotFoundExceptionThrow() {
        CommentDto commentDto = CommentDto.builder().text("Comment").build();
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> itemService.createComment(userId, itemId, commentDto));

        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void createCommentWhenUserNotBookingItemThenValidationException() {
        CommentDto commentDto = CommentDto.builder().text("Comment").build();
        UserDto userDto = UserDto.builder().build();
        User user = User.builder().build();
        when(userService.findById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(userMapper.userFromDto(userDto)).thenReturn(user);
        when(bookingRepository
                .findFirstByItemIdAndBookerIdAndStatusAndEndBefore(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        assertThrows(ValidationException.class,
                () -> itemService.createComment(userId, itemId, commentDto));

        verify(commentRepository, never()).save(any(Comment.class));
    }
}
