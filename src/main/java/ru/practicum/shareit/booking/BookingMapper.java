package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingSummaryDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static BookingResponseDto toDto(Booking booking) {
        return new BookingResponseDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getStatus(),
                new BookingResponseDto.ItemSummary(booking.getItem().getId(), booking.getItem().getName()),
                new BookingResponseDto.BookerSummary(booking.getBooker().getId()));
    }

    public static BookingSummaryDto toSummary(Booking booking) {
        return new BookingSummaryDto(booking.getId(), booking.getBooker().getId(),
                booking.getStart(), booking.getEnd());
    }
}
