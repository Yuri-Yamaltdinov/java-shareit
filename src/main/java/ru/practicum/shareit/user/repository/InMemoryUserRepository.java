package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private final Map<Long, User> usersStorage = new HashMap<>();
    private long lastId = 1;

    @Override
    public User create(User user) {
        user.setId(lastId++);
        usersStorage.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        if (usersStorage.containsKey(userId)) {
            return Optional.of(usersStorage.get(userId));
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return usersStorage.values()
                .stream()
                .sorted((o1, o2) -> (int) (o1.getId() - o2.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User update(Long userId, User user) {
        return usersStorage.put(userId, user);
    }

    @Override
    public void delete(Long userId) {
        usersStorage.remove(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return usersStorage.values().stream()
                .filter(u -> u.getEmail().equals(email))
                .findFirst();
    }

}
