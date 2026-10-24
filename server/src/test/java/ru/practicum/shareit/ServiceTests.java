package ru.practicum.shareit;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class ServiceTests {
    @Autowired
    private UserService users;

    @Autowired
    private ItemService items;

    @Autowired
    private BookingService bookings;

    @Test
    void userCrudAndEmailUniqueness() {
        UserDto user = user();
        assertThat(users.getAll()).extracting(UserDto::getId).contains(user.getId());
        UserDto patch = new UserDto();
        patch.setName("Updated");
        patch.setEmail(user.getEmail());
        assertThat(users.update(user.getId(), patch).getName()).isEqualTo("Updated");
        assertThat(users.get(user.getId()).getEmail()).isEqualTo(user.getEmail());
        UserDto second = user();
        users.delete(second.getId());
        assertThat(users.getAll()).extracting(UserDto::getId).doesNotContain(second.getId());
        UserDto duplicate = new UserDto();
        duplicate.setName("Duplicate");
        duplicate.setEmail(user.getEmail().toUpperCase());
        assertThatThrownBy(() -> users.create(duplicate)).isInstanceOf(ConflictException.class);
    }

    @Test
    void itemCrudSearchAndAvailability() {
        UserDto owner = user();
        UserDto other = user();
        assertThat(items.getAll(other.getId())).isEmpty();
        ItemDto input = item();
        var created = items.create(owner.getId(), input);
        assertThat(items.search(input.getName().toUpperCase())).extracting(ItemDto::getId).contains(created.getId());
        assertThat(items.search("  ")).isEmpty();
        ItemDto patch = new ItemDto();
        patch.setDescription("Updated description");
        patch.setAvailable(false);
        assertThat(items.update(owner.getId(), created.getId(), patch).getDescription())
                .isEqualTo("Updated description");
        assertThat(items.search(input.getName())).extracting(ItemDto::getId).doesNotContain(created.getId());
        assertThat(items.get(created.getId(), other.getId()).getAvailable()).isFalse();
        assertThatThrownBy(() -> items.get(Long.MAX_VALUE, owner.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void rejectedBookingAndUnavailableItem() {
        UserDto owner = user();
        UserDto renter = user();
        var created = items.create(owner.getId(), item());
        BookingDto dto = new BookingDto();
        dto.setItemId(created.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        var booking = bookings.create(renter.getId(), dto);
        assertThat(bookings.approve(owner.getId(), booking.id(), false).status()).isEqualTo(BookingStatus.REJECTED);
        assertThat(bookings.get(renter.getId(), booking.id()).status()).isEqualTo(BookingStatus.REJECTED);
        ItemDto patch = new ItemDto();
        patch.setAvailable(false);
        items.update(owner.getId(), created.getId(), patch);
        assertThatThrownBy(() -> bookings.create(renter.getId(), dto)).isInstanceOf(BadRequestException.class);
    }

    private UserDto user() {
        UserDto dto = new UserDto();
        dto.setName("User");
        dto.setEmail(UUID.randomUUID() + "@example.org");
        return users.create(dto);
    }

    private ItemDto item() {
        ItemDto dto = new ItemDto();
        dto.setName("Tool-" + UUID.randomUUID());
        dto.setDescription("Tool");
        dto.setAvailable(true);
        return dto;
    }
}
