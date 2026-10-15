package ru.practicum.shareit.user;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> entities = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong();

    @Override
    public User save(User entity) {
        if (entity.getId() == null) {
            entity.setId(sequence.incrementAndGet());
        }
        entities.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<User> findById(long id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public List<User> findAll() {
        return entities.values().stream().sorted(Comparator.comparing(User::getId)).toList();
    }

    @Override
    public void delete(long id) {
        entities.remove(id);
    }
}
