package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryItemRepository implements ItemRepository {

    private final Map<Long, Item> itemsStorage = new HashMap<>();
    private long lastId = 1;

    @Override
    public Item create(Item item) {
        item.setId(lastId++);
        itemsStorage.put(item.getId(), item);
        return item;
    }

    @Override
    public Optional<Item> findById(Long itemId) {
        if (itemsStorage.containsKey(itemId)) {
            return Optional.of(itemsStorage.get(itemId));
        }
        return Optional.empty();
    }

    @Override
    public List<Item> findAll(Long userId) {
        return itemsStorage.values().stream().filter(item -> item.getOwnerId().equals(userId)).collect(Collectors.toList());
    }

    @Override
    public Item update(Long itemId, Item item) {
        return itemsStorage.put(itemId, item);
    }

    @Override
    public void delete(Long itemId) {
        itemsStorage.remove(itemId);
    }

    @Override
    public List<Item> search(String text) {
        return itemsStorage.values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .sorted(Comparator.comparingLong(Item::getId))
                .collect(Collectors.toList());
    }
}
