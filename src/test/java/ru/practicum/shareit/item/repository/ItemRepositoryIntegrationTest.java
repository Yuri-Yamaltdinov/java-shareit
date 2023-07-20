package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


@DataJpaTest
public class ItemRepositoryIntegrationTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;


    @Test
    void pageableFindByOwnerId_whenInvoked_thenItemsForRequestedOwnerAndPageReturned() {
        User owner = saveRandomUser();
        PageRequest pageRequest = PageRequest.of(1, 1);
        itemRepository.save(Item.builder()
                .name("item_1")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        Page<Item> items = itemRepository.findAllByUserId(owner.getId(), pageRequest);

        assertThat(items.getTotalPages(), equalTo(2));
        assertThat(items.getTotalElements(), equalTo(2L));
        assertEquals(item2, items.getContent().get(0));
    }

    @Test
    void searchByText_whenInvoked_thenItemsFoundByTextInNameOrDescriptionCaseInsensitive() {
        User owner = saveRandomUser();
        PageRequest pageRequest = PageRequest.of(0, 3);
        String text = "ITEM_1";
        Item item1 = itemRepository.save(Item.builder()
                .name("item_1")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("addition to Item_1")
                .available(true)
                .owner(owner)
                .build());
        Item item3 = itemRepository.save(Item.builder()
                .name("item3")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        Page<Item> items = itemRepository.search(text, pageRequest);

        List<Item> itemsList = items.getContent();
        assertThat(items.getTotalPages(), equalTo(1));
        assertThat(items.getTotalElements(), equalTo(2L));
        assertEquals(item1, itemsList.get(0));
        assertEquals(item2, itemsList.get(1));
        assertFalse(itemsList.contains(item3));
    }

    @Test
    void findAll_whenInvoked_thenItemsWithRequestFound() {
        User owner = saveRandomUser();
        ItemRequest itemRequest = saveRandomRequest();
        PageRequest pageRequest = PageRequest.of(0, 10);
        Item item1 = itemRepository.save(Item.builder()
                .name("item1")
                .description("desc")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build());
        Item item2 = itemRepository.save(Item.builder()
                .name("item2")
                .description("addition to Item_1")
                .available(true)
                .owner(owner)
                .request(itemRequest)
                .build());
        Item item3 = itemRepository.save(Item.builder()
                .name("item3")
                .description("desc")
                .available(true)
                .owner(owner)
                .build());

        List<Item> foundItems = itemRepository.findAllByUserId(owner.getId(), pageRequest)
                                                    .stream()
                                                    .collect(Collectors.toList());

        assertThat(foundItems, hasSize(3));
        assertThat(foundItems.get(0), equalTo(item1));
        assertThat(foundItems.get(1), equalTo(item2));
        assertThat(foundItems, not(containsInAnyOrder(item3)));
    }

    private User saveRandomUser() {
        return userRepository.save(User.builder()
                .name("name")
                .email(String.format("%s%s@email.ru", "email", new Random(9999L)))
                .build());
    }

    private ItemRequest saveRandomRequest() {
        return itemRequestRepository.save(ItemRequest.builder()
                .requestor(saveRandomUser())
                .description("desc")
                .created(LocalDateTime.now())
                .build());
    }

}
