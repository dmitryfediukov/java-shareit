package ru.practicum.shareit.booking;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ItemSummary;
import ru.practicum.shareit.booking.dto.BookerSummary;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.booking.dto.BookingSummaryDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {
    public static Booking toModel(BookingDto dto, Item item, User booker) {
        Booking booking = new Booking();
        booking.setStart(dto.getStart());
        booking.setEnd(dto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    public static BookingResponseDto toDto(Booking booking) {
        return new BookingResponseDto(booking.getId(), booking.getStart(), booking.getEnd(), booking.getStatus(),
                new ItemSummary(booking.getItem().getId(), booking.getItem().getName()),
                new BookerSummary(booking.getBooker().getId()));
    }

    public static BookingSummaryDto toSummary(Booking booking) {
        return new BookingSummaryDto(booking.getId(), booking.getBooker().getId(),
                booking.getStart(), booking.getEnd());
    }
}
