package ru.practicum.shareit.request;

import java.util.List;
import ru.practicum.shareit.request.dto.ItemRequestDto;

public interface ItemRequestService {
    ItemRequestDto create(long userId, ItemRequestDto dto);

    List<ItemRequestDto> getOwn(long userId);

    List<ItemRequestDto> getAll(long userId, int from, int size);

    ItemRequestDto get(long userId, long requestId);
}
