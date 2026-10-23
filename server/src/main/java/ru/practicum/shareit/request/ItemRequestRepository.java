package ru.practicum.shareit.request;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> findByRequesterIdOrderByCreatedDescIdDesc(Long userId);

    List<ItemRequest> findByRequesterIdNotOrderByCreatedDescIdDesc(Long userId, Pageable pageable);
}
