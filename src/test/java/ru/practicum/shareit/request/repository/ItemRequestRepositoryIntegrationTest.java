package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
public class ItemRequestRepositoryIntegrationTest {

    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void testFindAllByRequestorId() {
        User requestor = saveRandomUser();
        ItemRequest request1 = itemRequestRepository.save(ItemRequest.builder()
                .created(LocalDateTime.now().minusHours(2))
                .description("request1")
                .requestor(requestor)
                .build());
        ItemRequest request2 = itemRequestRepository.save(ItemRequest.builder()
                .created(LocalDateTime.now().minusHours(1))
                .description("request2")
                .requestor(requestor)
                .build());

        List<ItemRequest> requests = itemRequestRepository.findByRequestorIdOrderByCreatedAsc(requestor.getId());

        assertThat(requests, hasSize(2));
        assertThat(requests.get(0), equalTo(request1));
        assertThat(requests.get(1), equalTo(request2));
    }

    @Test
    void testFindAllByRequestorIdPageable() {
        itemRequestRepository.deleteAll();
        User requestor = saveRandomUser();
        int page = 2;
        int size = 1;
        PageRequest pageRequest = PageRequest.of(page, size);
        ItemRequest request1 = itemRequestRepository.save(ItemRequest.builder()
                .created(LocalDateTime.now().minusHours(3))
                .description("request1")
                .requestor(requestor)
                .build());
        ItemRequest request2 = itemRequestRepository.save(ItemRequest.builder()
                .created(LocalDateTime.now().minusHours(2))
                .description("request2")
                .requestor(requestor)
                .build());
        ItemRequest request3 = itemRequestRepository.save(ItemRequest.builder()
                .created(LocalDateTime.now().minusHours(1))
                .description("request3")
                .requestor(requestor)
                .build());

        List<ItemRequest> result = itemRequestRepository.findByRequestorIdOrderByCreatedAsc(requestor.getId(), pageRequest);
        Page<ItemRequest> resultPage = new PageImpl<>(result, pageRequest, result.size());

        assertThat(resultPage.getTotalElements(), equalTo(3L));
        assertThat(resultPage.getTotalPages(), equalTo(3));
        assertThat(resultPage.getContent().get(0), equalTo(request3));
    }

    private User saveRandomUser() {
        return userRepository.save(User.builder()
                .name("name")
                .email(String.format("%s%s@email.ru", "email", new Random(9999L)))
                .build());
    }

}
