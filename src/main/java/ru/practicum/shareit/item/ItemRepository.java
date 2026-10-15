package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;
import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {
    Item save(Item entity);

    Optional<Item> findById(long id);

    List<Item> findAll();

    void delete(long id);
}
