package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AccessException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        userService.findById(userId);
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwnerId(userId);
        return ItemMapper.toDto(itemRepository.create(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        userService.findById(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException(Item.class, "Item id not found in storage"));

        if (!item.getOwnerId().equals(userId)) {
            throw new AccessException(String.format("User with id %d is not the owner of item with id %d",
                    userId, item.getId()));
        }

        item.setName(itemDto.getName() != null ? itemDto.getName() : item.getName());
        item.setDescription(itemDto.getDescription() != null ? itemDto.getDescription() : item.getDescription());
        item.setAvailable(itemDto.getAvailable() != null ? itemDto.getAvailable() : item.getAvailable());

        return ItemMapper.toDto(itemRepository.update(itemId, item));
    }

    @Override
    public ItemDto findById(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(Item.class, String.format("Item with id %d not found in storage",
                        itemId))
        );
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> findAll(Long userId) {
        userService.findById(userId);
        return itemRepository.findAll(userId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new EntityNotFoundException(Item.class, String.format("Item with id %d not found in storage",
                        itemId))
        );
        if (!item.getOwnerId().equals(userId)) {
            throw new AccessException(String.format("User with id %d is not the owner of item with id %d",
                    userId, item.getId()));
        }
        itemRepository.delete(itemId);

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
}
