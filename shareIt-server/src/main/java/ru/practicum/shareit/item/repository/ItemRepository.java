package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.Pagination;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query(value = "select it " +
            "from Item as it " +
            "join it.owner as u " +
            "where u.id = :userId ")
    Page<Item> findAllByUserId(@Param("userId") Long userId, Pagination page);

    @Query(value = "select i from Item i " +
            "where (upper(i.name) like upper(concat('%', :text, '%')) " +
            " or upper(i.description) like upper(concat('%', :text, '%')))" +
            " and i.available = true ")
    Page<Item> search(@Param ("text") String text, Pagination page);

}
