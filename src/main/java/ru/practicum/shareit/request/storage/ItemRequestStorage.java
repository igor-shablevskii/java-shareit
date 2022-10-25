package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Repository
public interface ItemRequestStorage extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequester(Long id);

    @Query("select ir from ItemRequest ir where ir.requester <> ?1")
    Page<ItemRequest> findItemRequestsFromAnotherUsers(Long id, Pageable pageable);
}
