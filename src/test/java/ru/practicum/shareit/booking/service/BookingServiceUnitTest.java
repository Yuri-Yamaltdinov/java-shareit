package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInitial;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceUnitTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Captor
    private ArgumentCaptor<Booking> bookingArgumentCaptor;
    @Captor
    private ArgumentCaptor<BookingState> bookingStatusArgumentCaptor;
    private Long userId;
    private Long bookingId;
    private Booking booking;
    private BookingDtoInitial bookingDtoInitial;
    private BookingDto bookingDtoResponse;

    private User user;
    private Item item;
    private User booker;
    private User owner;


    @BeforeEach
    void beforeEach() {
        userId = 0L;
        bookingId = 0L;
        booking = Booking.builder().build();
        bookingDtoInitial = BookingDtoInitial.builder()
                .end(LocalDateTime.MAX)
                .start(LocalDateTime.MIN).build();
        bookingDtoResponse = BookingDto.builder().build();
        user = User.builder().id(1L).build();
        item = Item.builder().owner(user).available(true).build();
        booker = User.builder().id(0L).build();
        owner = User.builder().id(0L).build();
    }

    @Test
    void createWhenInvokeThenReturnBookingResponseDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));
        when(bookingMapper.bookingFromDtoInitial(any(), any(), any())).thenReturn(booking);
        when(bookingMapper.bookingToDto(any())).thenReturn(bookingDtoResponse);

        BookingDto actualBooking = bookingService.create(userId, bookingDtoInitial);

        assertNotNull(actualBooking);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());

        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertNotNull(savedBooking.getBooker());
        assertNotNull(savedBooking.getItem());
        assertEquals(BookingState.WAITING, savedBooking.getStatus());
    }

    @Test
    void createWhenStartAfterEndThenValidationExceptionThrow() {
        bookingDtoInitial.setStart(LocalDateTime.MAX);
        bookingDtoInitial.setEnd(LocalDateTime.MIN);

        assertThrows(ValidationException.class,
                () -> bookingService.create(userId, bookingDtoInitial));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createWhenItemNotAvailableThenValidationExceptionThrow() {
        item.setAvailable(false);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        assertThrows(ValidationException.class,
                () -> bookingService.create(userId, bookingDtoInitial));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createWhenUserIsOwnerThenEntityNotFoundExceptionThrow() {
        user.setId(0L);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.ofNullable(item));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(userId, bookingDtoInitial));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createWhenUserNotFoundThenEntityNotFoundExceptionThrow() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(userId, bookingDtoInitial));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void createWhenItemNotFoundThenEntityNotFoundExceptionThrow() {
        user.setId(0L);
        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.create(userId, bookingDtoInitial));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void bookingConfirmationWhenInvokeWithApprovedTrueThenReturnBookingResponseDtoWithStatusAPPROVED() {
        user.setId(0L);
        booking.setStatus(BookingState.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToDto(any())).thenReturn(bookingDtoResponse);

        BookingDto actualBooking = bookingService.setStatus(userId, bookingId, true);

        assertNotNull(actualBooking);

        verify(bookingMapper).bookingToDto(bookingArgumentCaptor.capture());

        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(BookingState.APPROVED, savedBooking.getStatus());
    }

    @Test
    void setStatusWhenInvokeWithApprovedFalseThenReturnBookingResponseDtoWithStatusREJECTED() {
        user.setId(0L);
        booking.setStatus(BookingState.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToDto(any())).thenReturn(bookingDtoResponse);

        BookingDto actualBooking = bookingService.setStatus(userId, bookingId, false);

        assertNotNull(actualBooking);

        verify(bookingMapper).bookingToDto(bookingArgumentCaptor.capture());

        Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(BookingState.REJECTED, savedBooking.getStatus());
    }

    @Test
    void setStatusWhenBookingNotFoundThenEntityNotFoundExceptionThrow() {
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.setStatus(userId, bookingId, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void setStatusWhenUserNotOwnerThenEntityNotFoundExceptionThrow() {
        booking.setStatus(BookingState.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.setStatus(userId, bookingId, true));
        verify(bookingRepository, never()).save(any());
    }


    @Test
    void setStatusWhenBookingStatusApprovedThenValidationExceptionThrow() {
        user.setId(0L);
        booking.setStatus(BookingState.APPROVED);
        booking.setItem(item);
        booking.setBooker(user);
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        assertThrows(ValidationException.class,
                () -> bookingService.setStatus(userId, bookingId, true));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void findAllByStateWhenWrongStateThenValidationExceptionThrow() {
        String state = "wrong";

        assertThrows(ValidationException.class,
                () -> bookingService.findAllByState(userId, state, 1, 1));
    }

    @Test
    void findAllByStateWhenOwnerAndCURRENTStateThenInvokeFindByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc() {
        String state = "CURRENT";

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(),
                        any(),
                        any(),
                        any());
    }

    @Test
    void findAllByStateWhenOwnerAndFUTUREStateThenInvokeFindByItemOwnerIdAndStartAfterOrderByStartDesc() {
        String state = "FUTURE";

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void findAllByStateWhenOwnerAndPASTStateThenInvokeFindByItemOwnerIdAndEndBeforeOrderByStartDesc() {
        String state = "PAST";

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void findAllByStateWhenOwnerAndWAITINGStateThenInvokeFindByItemOwnerIdAndStatusOrderByStartDescWithWaitingInParams() {
        String state = "WAITING";

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                        bookingStatusArgumentCaptor.capture(),
                        any());

        BookingState status = bookingStatusArgumentCaptor.getValue();
        assertEquals(BookingState.WAITING, status);
    }

    @Test
    void findAllByStateWhenOwnerAndREJECTEDStateThenInvokeFindByItemOwnerIdAndStatusOrderByStartDescWithRejectedInParams() {
        String state = "REJECTED";

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                        bookingStatusArgumentCaptor.capture(),
                        any());

        BookingState status = bookingStatusArgumentCaptor.getValue();
        assertEquals(BookingState.REJECTED, status);
    }

    @Test
    void findAllByStateWhenOwnerAndAllStateThenInvokeFindByItemOwnerIdOrderByStartDesc() {
        String state = "ALL";

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(),
                        any());
    }

    @Test
    void findAllByStateWhenNotOwnerAndCURRENTStateThenInvokeFindByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc() {
        String state = "CURRENT";

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(anyLong(),
                        any(),
                        any(),
                        any());
    }

    @Test
    void findAllByStateWhenNotOwnerAndFUTUREStateThenInvokeFindAllByBookerIdAndStartAfterOrderByStartDesc() {
        String state = "FUTURE";

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStartAfterOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void findAllByStateWhenNotOwnerAndPASTStateThenInvokeFindAllByBookerIdAndEndBeforeOrderByStartDesc() {
        String state = "PAST";
        when(userService.findById(anyLong())).thenReturn(new UserDto());


        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndEndBeforeOrderByStartDesc(anyLong(),
                        any(),
                        any());
    }

    @Test
    void findAllByStateWhenNotOwnerAndWAITINGStateThenInvokeFindAllByBookerIdAndStatusOrderByStartDescWithWaitingInParams() {
        String state = "WAITING";
        when(userService.findById(anyLong())).thenReturn(new UserDto());

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                        bookingStatusArgumentCaptor.capture(),
                        any());

        BookingState status = bookingStatusArgumentCaptor.getValue();
        assertEquals(BookingState.WAITING, status);
    }

    @Test
    void findAllByStateWhenNotOwnerAndREJECTEDStateThenInvokeFindAllByBookerIdAndStatusOrderByStartDescWithRejectedInParams() {
        String state = "REJECTED";
        when(userService.findById(anyLong())).thenReturn(new UserDto());

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdAndStatusOrderByStartDesc(anyLong(),
                        bookingStatusArgumentCaptor.capture(),
                        any());

        BookingState status = bookingStatusArgumentCaptor.getValue();
        assertEquals(BookingState.REJECTED, status);
    }

    @Test
    void findAllByStateWhenNotOwnerAndAllStateThenInvokeFindAllByBookerIdOrderByStartDesc() {
        String state = "ALL";
        when(userService.findById(anyLong())).thenReturn(new UserDto());

        bookingService.findAllByState(userId, state, 1, 1);
        verify(bookingRepository, times(1))
                .findAllByBookerIdOrderByStartDesc(anyLong(),
                        any());
    }

    @Test
    void findByIdWhenInvokeThenReturnBookingDto() {
        booking.setStatus(BookingState.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        when(userService.findById(anyLong())).thenReturn(new UserDto());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToDto(booking)).thenReturn(bookingDtoResponse);

        BookingDto actualBooking = bookingService.findById(userId, bookingId);

        assertNotNull(actualBooking);
        assertEquals(bookingDtoResponse, actualBooking);
    }

    @Test
    void findByIdWhenBookingNotFoundThenEntityNotFoundExceptionThrow() {
        when(userService.findById(anyLong())).thenReturn(new UserDto());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> bookingService.findById(userId, bookingId));
    }

    @Test
    void findByIdWhenUserNotBookerThenReturnBookingDto() {
        item.setOwner(owner);
        booking.setStatus(BookingState.WAITING);
        booking.setItem(item);
        booking.setBooker(user);
        when(userService.findById(anyLong())).thenReturn(new UserDto());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToDto(booking)).thenReturn(bookingDtoResponse);

        BookingDto actualBooking = bookingService.findById(userId, bookingId);

        assertNotNull(actualBooking);
        assertEquals(bookingDtoResponse, actualBooking);
    }

    @Test
    void findByIdWhenUserNotOwnerThenReturnBookingDto() {
        booking.setStatus(BookingState.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        when(userService.findById(anyLong())).thenReturn(new UserDto());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));
        when(bookingMapper.bookingToDto(booking)).thenReturn(bookingDtoResponse);

        BookingDto actualBooking = bookingService.findById(userId, bookingId);

        assertNotNull(actualBooking);
        assertEquals(bookingDtoResponse, actualBooking);
    }

    @Test
    void findByIdWhenUserNotOwnerAndNotBookerThenEntityNotFoundExceptionThrow() {
        booker.setId(1L);
        owner.setId(2L);
        item.setOwner(owner);
        booking.setStatus(BookingState.WAITING);
        booking.setItem(item);
        booking.setBooker(booker);
        when(userService.findById(anyLong())).thenReturn(new UserDto());
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.ofNullable(booking));

        assertThrows(EntityNotFoundException.class,
                () -> bookingService.findById(userId, bookingId));
    }

}
