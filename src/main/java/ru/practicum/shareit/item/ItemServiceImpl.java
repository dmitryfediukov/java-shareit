package ru.practicum.shareit.item;

import java.util.List;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(long userId, ItemDto dto) {
        userService.get(userId);
        Item item = ItemMapper.toModel(dto);
        item.setId(null);
        item.setOwner(userId);
        return ItemMapper.toDto(repository.save(item));
    }

    @Override
    @Transactional
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
    public ItemDto get(long itemId, Long userId) {
        return enrich(List.of(requireItem(itemId)), userId).getFirst();
    }

    @Override
    public List<ItemDto> getAll(long userId) {
        userService.get(userId);
        return enrich(repository.findByOwnerOrderByIdAsc(userId), userId);
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return repository.search(text).stream().map(ItemMapper::toDto).toList();
    }


    @Override
    @Transactional
    public CommentDto addComment(long userId, long itemId, CommentDto dto) {
        userService.get(userId);
        Item item = requireItem(itemId);
        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndLessThanEqual(
                itemId, userId, BookingStatus.APPROVED, LocalDateTime.now())) {
            throw new BadRequestException("Only a user who completed a booking can leave a comment");
        }
        Comment comment = new Comment();
        comment.setText(dto.getText());
        comment.setItem(item);
        comment.setAuthor(userRepository.findById(userId).orElseThrow());
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toDto(commentRepository.save(comment));
    }

    private List<ItemDto> enrich(List<Item> items, Long userId) {
        if (items.isEmpty()) {
            return List.of();
        }
        List<Long> ids = items.stream().map(Item::getId).toList();
        Map<Long, List<Comment>> comments = commentRepository.findByItemIdInOrderByCreatedAscIdAsc(ids)
                .stream().collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        List<Long> ownedIds = items.stream().filter(item -> item.getOwner().equals(userId))
                .map(Item::getId).toList();
        Map<Long, List<Booking>> bookings = ownedIds.isEmpty() ? Map.of()
                : bookingRepository.findApprovedForItems(ownedIds).stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        LocalDateTime now = LocalDateTime.now();
        List<ItemDto> result = new ArrayList<>();
        for (Item item : items) {
            ItemDto dto = ItemMapper.toDto(item);
            dto.setComments(comments.getOrDefault(item.getId(), List.of()).stream()
                    .map(CommentMapper::toDto).toList());
            for (Booking booking : bookings.getOrDefault(item.getId(), List.of())) {
                if (!booking.getStart().isAfter(now)) {
                    dto.setLastBooking(BookingMapper.toSummary(booking));
                } else if (dto.getNextBooking() == null) {
                    dto.setNextBooking(BookingMapper.toSummary(booking));
                }
            }
            result.add(dto);
        }
        return result;
    }

    private Item requireItem(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Item not found: " + id));
    }
}
