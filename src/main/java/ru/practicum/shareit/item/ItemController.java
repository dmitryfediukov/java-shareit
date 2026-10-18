package ru.practicum.shareit.item;

import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.validation.Create;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemService service;

    @PostMapping
    public ItemDto create(@RequestHeader(USER_HEADER) long userId,
                          @Validated({Create.class, Default.class}) @RequestBody ItemDto dto) {
        return service.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId,
                          @Valid @RequestBody ItemDto dto) {
        return service.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable long itemId, @RequestHeader(value = USER_HEADER, required = false) Long userId) {
        return service.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(USER_HEADER) long userId) {
        return service.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return service.search(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(USER_HEADER) long userId, @PathVariable long itemId,
                                 @Valid @RequestBody CommentDto dto) {
        return service.addComment(userId, itemId, dto);
    }
}
