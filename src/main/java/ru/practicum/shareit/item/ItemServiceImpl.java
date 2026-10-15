package ru.practicum.shareit.item;

import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;

    @Override
    public ItemDto create(long userId, ItemDto dto) {
        userService.get(userId);
        Item item = ItemMapper.toModel(dto);
        item.setId(null);
        item.setOwner(userId);
        return ItemMapper.toDto(repository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto dto) {
        userService.get(userId);
        Item item = requireItem(itemId);
        if (!item.getOwner().equals(userId)) {
            throw new NotFoundException("Item does not belong to user: " + userId);
        }
        if (dto.getName() != null) {
            item.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            item.setDescription(dto.getDescription());
        }
        if (dto.getAvailable() != null) {
            item.setAvailable(dto.getAvailable());
        }
        return ItemMapper.toDto(repository.save(item));
    }

    @Override
    public ItemDto get(long itemId) {
        return ItemMapper.toDto(requireItem(itemId));
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        userService.get(userId);
        return repository.findAll().stream().filter(item -> item.getOwner().equals(userId))
                .map(ItemMapper::toDto).toList();
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        String query = text.toLowerCase(Locale.ROOT);
        return repository.findAll().stream().filter(item -> Boolean.TRUE.equals(item.getAvailable()))
                .filter(item -> item.getName().toLowerCase(Locale.ROOT).contains(query)
                        || item.getDescription().toLowerCase(Locale.ROOT).contains(query))
                .map(ItemMapper::toDto).toList();
    }

    private Item requireItem(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Item not found: " + id));
    }
}
