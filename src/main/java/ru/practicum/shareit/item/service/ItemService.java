package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookingsAndComments;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long itemId, ItemDto itemDto);

    ItemDtoWithBookingsAndComments findById(Long userId, Long itemId);

    List<ItemDtoWithBookingsAndComments> findAll(Long userId);

    void delete(Long userId, Long itemId);

    List<ItemDto> search(Long userId, String text);

    CommentDto createComment(Long userId, Long itemId, CommentDto commentDto);
}
