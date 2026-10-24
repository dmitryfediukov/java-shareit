package ru.practicum.shareit.item;

import java.util.List;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

public interface ItemService {
    ItemDto create(long userId, ItemDto dto);

    ItemDto update(long userId, long itemId, ItemDto dto);

    ItemDto get(long itemId, Long userId);

    List<ItemDto> getAll(long userId);

    List<ItemDto> search(String text);

    CommentDto addComment(long userId, long itemId, CommentDto dto);
}
