package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select it " +
            "from Item as it " +
            "join it.owner as u " +
            "where u.id = ?1 ")
    List<Item> findAllByUserId(Long userId);

    @Query(value = "select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))" +
            " and i.available = true ")
    List<Item> search(String text);

}
