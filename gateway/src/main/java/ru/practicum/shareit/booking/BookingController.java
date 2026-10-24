package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
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
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> create(@Positive @RequestHeader(USER_HEADER) long userId,
            @Valid @RequestBody BookItemRequestDto dto) {
        return client.bookItem(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@Positive @RequestHeader(USER_HEADER) long userId,
            @Positive @PathVariable long bookingId, @RequestParam boolean approved) {
        return client.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> get(@Positive @RequestHeader(USER_HEADER) long userId,
            @Positive @PathVariable long bookingId) {
        return client.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@Positive @RequestHeader(USER_HEADER) long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        return client.getBookings(userId, parseState(state), from, size, false);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@Positive @RequestHeader(USER_HEADER) long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        return client.getBookings(userId, parseState(state), from, size, true);
    }

    private BookingState parseState(String state) {
        return BookingState.from(state).orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
    }
}
