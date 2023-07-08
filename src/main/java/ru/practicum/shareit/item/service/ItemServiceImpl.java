package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.BookingState;
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
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = UserMapper.fromUserDto(userService.findById(userId));
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwner(user);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userService.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Item.class, "Item id not found in storage"));

        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessException(String.format("User with id %d is not the owner of item with id %d",
                    userId, item.getId()));
        }

        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable());

        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    public ItemDtoWithBookingsAndComments findById(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(Item.class, String.format("Item with id %d not found in storage",
                        itemId))
        );

        BookingInfoDto lastBookingDto = null;
        BookingInfoDto nextBookingDto = null;

        if (item.getOwner().getId().equals(userId)) {

            lastBookingDto = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                            item.getId(),
                            BookingState.APPROVED,
                            LocalDateTime.now())
                    .map(BookingMapper::bookingToInfoDto)
                    .orElse(null);

            nextBookingDto = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                            item.getId(),
                            BookingState.APPROVED,
                            LocalDateTime.now())
                    .map(BookingMapper::bookingToInfoDto)
                    .orElse(null);
        }

        List<CommentDto> comments = commentRepository.findByItemIdOrderByCreatedDesc(itemId).stream()
                .map(commentMapper::commentToDto)
                .collect(Collectors.toList());

        return ItemMapper.toItemDtoWithBookingAndComments(item, lastBookingDto, nextBookingDto, comments);
    }

    @Override
    public List<ItemDtoWithBookingsAndComments> findAll(Long userId) {
        userService.findById(userId);
        List<ItemDtoWithBookingsAndComments> items = itemRepository.findAllByUserId(userId)
                .stream()
                .map(item -> {
                    BookingInfoDto lastBookingDto = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByEndDesc(
                                    item.getId(), BookingState.APPROVED, LocalDateTime.now())
                            .map(BookingMapper::bookingToInfoDto)
                            .orElse(null);
                    BookingInfoDto nextBookingDto = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStartAsc(
                                    item.getId(), BookingState.APPROVED, LocalDateTime.now())
                            .map(BookingMapper::bookingToInfoDto)
                            .orElse(null);

                    List<CommentDto> comments = commentRepository.findByItem(item).stream()
                            .map(commentMapper::commentToDto)
                            .collect(Collectors.toList());

                    return ItemMapper.toItemDtoWithBookingAndComments(item, lastBookingDto, nextBookingDto, comments);
                })
                .sorted(Comparator.comparingLong(ItemDtoWithBookingsAndComments::getId))
                .collect(Collectors.toList());
        return items;
    }

    @Override
    public void delete(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(Item.class, String.format("Item with id %d not found in storage",
                        itemId))
        );
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessException(String.format("User with id %d is not the owner of item with id %d",
                    userId, item.getId()));
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        userService.findById(userId);
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.search(text);
        if (items.isEmpty()) {
            throw new EntityNotFoundException(Item.class, String.format("text: %s", text));
        }
        return items.stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        User author = UserMapper.fromUserDto(userService.findById(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(Item.class, String.format("Item with id %d not found in storage",
                        itemId))
        );
        bookingRepository.findFirstByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingState.APPROVED, LocalDateTime.now())
                .orElseThrow(() -> new ValidationException(String.format("User with id %d did not rent item with id %d", userId, itemId)));
        Comment comment = commentMapper.commentFromDto(commentDto);
        comment.setItem(item);
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.commentToDto(commentRepository.save(comment));
    }

}
