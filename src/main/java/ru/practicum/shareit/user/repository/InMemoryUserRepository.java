package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.EntityNotFoundException;
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
        findMatch(user).ifPresent(user1 -> {
            throw new ConflictException("User is already existing in storage");
        });
        String email = user.getEmail();
        if (usersStorage.values().stream()
                .map(User::getEmail)
                .anyMatch(storedEmail -> storedEmail.equals(email))) {
            throw new ConflictException("User email is already existing in storage");
        }
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
        findMatch(user).ifPresent(user1 -> {
            if (!userId.equals(user1.getId())) {
                throw new ConflictException("User duplicates existing entity");
            }
        });

        User originalUser = findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class, "User id not found in storage"));
        originalUser.setName(user.getName() != null ? user.getName() : originalUser.getName());
        originalUser.setEmail(user.getEmail() != null ? user.getEmail() : originalUser.getEmail());
        return usersStorage.put(originalUser.getId(), originalUser);
    }

    @Override
    public void delete(Long userId) {
        usersStorage.remove(userId);
    }

    private Optional<User> findMatch(User user) {
        return usersStorage.values()
                .stream()
                .filter(user::equals)
                .findFirst();
    }
}
