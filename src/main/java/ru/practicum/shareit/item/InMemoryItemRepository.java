package ru.practicum.shareit.item;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> entities = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public Item save(Item entity) {
        if (entity.getId() == null) {
            entity.setId(sequence.incrementAndGet());
        }
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Item> findById(long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<Item> findAll() {
        return entities.values().stream().sorted(Comparator.comparing(Item::getId)).toList();
    }

    @Override
    public void delete(long id) {
        entities.remove(id);
    }
}
