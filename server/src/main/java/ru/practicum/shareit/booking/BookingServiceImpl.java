package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.util.List;
import ru.practicum.shareit.common.OffsetPageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository repository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public BookingResponseDto create(long userId, BookingDto dto) {
        User booker = requireUser(userId);
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + dto.getItemId()));
        if (item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }
        if (!Boolean.TRUE.equals(item.getAvailable())) {
            throw new BadRequestException("Item is unavailable");
        }
        if (dto.getStart() == null || dto.getEnd() == null || !dto.getStart().isBefore(dto.getEnd())
                || !dto.getStart().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("Invalid booking dates");
        }
        Booking booking = BookingMapper.toModel(dto, item, booker);
        return BookingMapper.toDto(repository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto approve(long userId, long bookingId, boolean approved) {
        Booking booking = requireBooking(bookingId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new ForbiddenException("Only item owner can approve booking");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Booking has already been processed");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        return BookingMapper.toDto(booking);
    }

    @Override
    public BookingResponseDto get(long userId, long bookingId) {
        Booking booking = requireBooking(bookingId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("Booking is not accessible to user");
        }
        return BookingMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAll(long userId, String state, boolean owner, int from, int size) {
        BookingState parsed;
        try {
            parsed = BookingState.valueOf(state);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Unknown state: " + state);
        }
        requireUser(userId);
        return repository.findForUser(userId, owner, parsed.name(), LocalDateTime.now(), new OffsetPageRequest(from, size))
                .stream().map(BookingMapper::toDto).toList();
    }

    private User requireUser(long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private Booking requireBooking(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("Booking not found: " + id));
    }
}
