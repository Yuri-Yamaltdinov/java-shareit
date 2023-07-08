package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInitial;
import ru.practicum.shareit.booking.dto.BookingStateDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingDtoInitial bookingDtoInitial) {
        if (bookingDtoInitial.getEnd().isBefore(bookingDtoInitial.getStart()) ||
                bookingDtoInitial.getEnd().equals(bookingDtoInitial.getStart())) {
            throw new ValidationException("Booking end date is before start date.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", userId)));
        Item item = itemRepository.findById(bookingDtoInitial.getItemId())
                .orElseThrow(() -> new EntityNotFoundException(Item.class, String.format("ID: %s", bookingDtoInitial.getItemId())));

        if (!item.getAvailable()) {
            throw new ValidationException("Item is unavailable.");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(Item.class, "User cannot book own item.");
        }

        Booking booking = BookingMapper.bookingFromDtoInitial(bookingDtoInitial, user, item);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingState.WAITING);

        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto setStatus(Long userId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(Booking.class, "Booking id not found in storage"));
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(Booking.class, "User is not the owner of the item.");
        }
        if (booking.getStatus().equals(BookingState.APPROVED)) {
            throw new ValidationException("Booking is already approved.");
        }
        booking.setStatus((approved) ? BookingState.APPROVED : BookingState.REJECTED);
        return BookingMapper.bookingToDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findById(Long userId, Long bookingId) {
        userService.findById(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(
                () -> new EntityNotFoundException(Booking.class, "Booking id not found in storage"));
        if (!booking.getBooker().getId().equals(userId) &&
                !booking.getItem().getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException(Booking.class, "User is not the owner or booker of the item.");
        }
        return BookingMapper.bookingToDto(booking);
    }

    @Override
    public List<BookingDto> findAllByState(Long userId, String state) {
        userService.findById(userId);
        BookingStateDto bookingStateDto;
        List<Booking> bookings;
        try {
            bookingStateDto = BookingStateDto.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }

        switch (bookingStateDto) {
            case CURRENT:
                bookings = bookingRepository.findByBookerIdAndStartBeforeAndEndAfterOrderByStartAsc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingState.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingState.REJECTED);
                break;
            default:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
        }

        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> findAllByItemOwner(Long userId, String state) {
        userService.findById(userId);
        BookingStateDto bookingStateDto;
        List<Booking> bookings;
        try {
            bookingStateDto = BookingStateDto.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }

        if (bookingRepository.findByItemOwnerIdOrderByStartDesc(userId).isEmpty()) {
            throw new ValidationException("User doesn't have booked items.");
        }

        switch (bookingStateDto) {
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(userId,
                        LocalDateTime.now(),
                        LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerIdAndStartAfterOrderByStartDesc(userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingState.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(userId, BookingState.REJECTED);
                break;
            default:
                bookings = bookingRepository.findByItemOwnerIdOrderByStartDesc(userId);
                break;
        }
        return bookings.stream()
                .map(BookingMapper::bookingToDto)
                .collect(Collectors.toList());
    }
}
