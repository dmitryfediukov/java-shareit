package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.validation.Create;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader(USER_HEADER) long userId,
            @Validated({Create.class, Default.class}) @RequestBody ItemDto dto) {
        return client.create(userId, dto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Positive @RequestHeader(USER_HEADER) long userId,
            @Positive @PathVariable long itemId, @Valid @RequestBody ItemDto dto) {
        return client.update(userId, itemId, dto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(value = USER_HEADER, required = false) Long userId,
            @Positive @PathVariable long itemId) {
        return client.getOne(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@Positive @RequestHeader(USER_HEADER) long userId) {
        return client.getAll(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@Positive @RequestHeader(USER_HEADER) long userId, @RequestParam String text) {
        return client.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> comment(@Positive @RequestHeader(USER_HEADER) long userId,
            @Positive @PathVariable long itemId, @Valid @RequestBody CommentDto dto) {
        return client.comment(userId, itemId, dto);
    }
}
