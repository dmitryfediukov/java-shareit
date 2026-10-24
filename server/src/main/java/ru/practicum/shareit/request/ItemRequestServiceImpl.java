package ru.practicum.shareit.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.OffsetPageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserRepository users;
    private final ItemRepository items;

    @Override
    @Transactional
    public ItemRequestDto create(long userId, ItemRequestDto dto) {
        ItemRequest request = ItemRequestMapper.toModel(dto, requireUser(userId), LocalDateTime.now());
        return ItemRequestMapper.toDto(repository.save(request), List.of());
    }

    @Override
    public List<ItemRequestDto> getOwn(long userId) {
        requireUser(userId);
        return enrich(repository.findByRequesterIdOrderByCreatedDescIdDesc(userId));
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        requireUser(userId);
        return enrich(repository.findByRequesterIdNotOrderByCreatedDescIdDesc(userId,
                new OffsetPageRequest(from, size)));
    }

    @Override
    public ItemRequestDto get(long userId, long requestId) {
        requireUser(userId);
        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));
        return enrich(List.of(request)).getFirst();
    }

    private User requireUser(long userId) {
        return users.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));
    }

    private List<ItemRequestDto> enrich(List<ItemRequest> requests) {
        if (requests.isEmpty()) {
            return List.of();
        }
        List<Long> ids = requests.stream().map(ItemRequest::getId).toList();
        Map<Long, List<Item>> responses = items.findByRequestIdInOrderByIdAsc(ids).stream()
                .collect(Collectors.groupingBy(item -> item.getRequest().getId()));
        return requests.stream().map(request -> ItemRequestMapper.toDto(request,
                responses.getOrDefault(request.getId(), List.of()).stream()
                        .map(item -> new RequestItemDto(item.getId(), item.getName(), item.getOwner().getId()))
                        .toList())).toList();
    }
}
