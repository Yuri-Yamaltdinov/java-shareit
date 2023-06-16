package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemoryUserRepository implements UserRepository {

    private Map<Long, User> usersStorage = new HashMap<>();
    private long lastId = 0;

    @Override
    public User create(User user) {
        findMatch(user).ifPresent(user1 -> {
            throw new ValidationException("User is already existing in storage");
        });
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
                throw new CreateDuplicateEntityException(User.class, user1.getId());
            }
        });

        User oldUser = findById(userId).orElseThrow(() -> new EntityNotFoundException(User.class, String.format("ID: %s", userId.toString())));
        oldUser.setName(user.getName() != null ? user.getName() : oldUser.getName());
        oldUser.setEmail(user.getEmail() != null ? user.getEmail() : oldUser.getEmail());
        return storage.put(oldUser.getId(), oldUser);
    }

    @Override
    public void delete(Long userId) {

    }

    private Optional<User> findMatch(User user) {
        return usersStorage.values()
                .stream()
                .filter(user::equals)
                .findFirst();
    }
}
