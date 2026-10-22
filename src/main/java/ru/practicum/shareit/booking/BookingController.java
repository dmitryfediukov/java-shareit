package ru.practicum.shareit.booking;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.GetMapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_HEADER = "X-Sharer-User-Id";
    private final BookingService service;

    @PostMapping
    public BookingResponseDto create(@RequestHeader(USER_HEADER) long userId, @Valid @RequestBody BookingDto dto) {
        return service.create(userId, dto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto approve(@RequestHeader(USER_HEADER) long userId, @PathVariable long bookingId,
                                      @RequestParam boolean approved) {
        return service.approve(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto get(@RequestHeader(USER_HEADER) long userId, @PathVariable long bookingId) {
        return service.get(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAll(@RequestHeader(USER_HEADER) long userId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        return service.getAll(userId, state, false);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getOwnerBookings(@RequestHeader(USER_HEADER) long userId,
                                                    @RequestParam(defaultValue = "ALL") String state) {
        return service.getAll(userId, state, true);
    }
}
