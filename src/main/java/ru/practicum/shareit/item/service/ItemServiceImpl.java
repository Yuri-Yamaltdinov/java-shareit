package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto create(ItemDto itemDto) {
        return null;
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userService.findById(userId);
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toDto(itemRepository.create(item));
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        userService.findById(userId);
        Item requestedItem = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException("Item with id {} does not exist", itemId)
        );
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> findAll(Long userId) {
        return null;
    }

    @Override
    public void delete(Long userId, Long itemId) {

    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        return null;
    }
}
