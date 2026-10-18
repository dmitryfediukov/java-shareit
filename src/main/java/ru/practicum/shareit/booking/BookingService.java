package ru.practicum.shareit.booking;

import java.util.List;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

public interface BookingService {
    BookingResponseDto create(long userId, BookingDto dto);

    BookingResponseDto approve(long userId, long bookingId, boolean approved);

    BookingResponseDto get(long userId, long bookingId);

    List<BookingResponseDto> getAll(long userId, String state, boolean owner);
}
