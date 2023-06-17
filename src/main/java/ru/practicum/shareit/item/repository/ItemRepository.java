package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item create(Item item);

    Optional<Item> findById(Long id);

    List<Item> findAll(Long userId);

    Item update(Long itemId, Item item);

    void delete(Long itemId);

    List<Item> search(String text);
}
