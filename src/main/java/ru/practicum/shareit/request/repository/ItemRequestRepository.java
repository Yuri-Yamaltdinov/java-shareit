package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.util.Pagination;

import java.util.List;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findByRequestorIdOrderByCreatedAsc(Long requestorId);

    List<ItemRequest> findByRequestorIdNotOrderByCreatedAsc(Long requestorId, Pagination page);

    List<ItemRequest> findByRequestorIdOrderByCreatedAsc(Long requestorId, Pagination page);

    @Query("select distinct request from ItemRequest as request " +
            "where request.requestor.id <> ?1 " +
            "order by request.created asc")
    List<ItemRequest> findAllExceptRequestorIdOrderByCreatedAsc(Long requestorId, Pagination page);
}
