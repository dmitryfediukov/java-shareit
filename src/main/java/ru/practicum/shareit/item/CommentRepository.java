package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(attributePaths = {"author", "item"})
    List<Comment> findByItemIdInOrderByCreatedAscIdAsc(Collection<Long> itemIds);
}
