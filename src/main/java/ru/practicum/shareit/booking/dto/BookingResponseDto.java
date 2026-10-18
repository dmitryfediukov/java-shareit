package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import ru.practicum.shareit.booking.BookingStatus;

public record BookingResponseDto(Long id, LocalDateTime start, LocalDateTime end, BookingStatus status,
                                 ItemSummary item, BookerSummary booker) {
    public record ItemSummary(Long id, String name) {
    }

    public record BookerSummary(Long id) {
    }
}
