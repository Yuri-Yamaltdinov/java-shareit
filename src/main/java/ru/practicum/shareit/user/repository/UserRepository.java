package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    Optional<User> findById(Long userId);

    List<User> findAll();

    User update(Long userId, User user);

    void delete(Long userId);

}
