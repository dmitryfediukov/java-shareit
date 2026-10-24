package ru.practicum.shareit.booking;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

public interface BookingService {
    BookingResponseDto create(long userId, BookingDto dto);

    BookingResponseDto approve(long userId, long bookingId, boolean approved);

    BookingResponseDto get(long userId, long bookingId);

    default List<BookingResponseDto> getAll(long userId, String state, boolean owner) {
        return getAll(userId, state, owner, 0, Integer.MAX_VALUE);
    }

    List<BookingResponseDto> getAll(long userId, String state, boolean owner, int from, int size);
}
