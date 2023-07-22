package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.Pagination;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select it " +
            "from Item as it " +
            "join it.owner as u " +
            "where u.id = ?1 ")
    Page<Item> findAllByUserId(Long userId, Pagination page);

    @Query(value = "select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%')))" +
            " and i.available = true ")
    Page<Item> search(String text, Pagination page);

}
