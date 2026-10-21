package ru.practicum.shareit;

import java.time.LocalDateTime;
import jakarta.persistence.EntityManager;
import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookingTests {
    @Autowired
    private BookingService bookings;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository users;

    @Autowired
    private ItemRepository items;

    @Autowired
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private EntityManager entityManager;

    private User owner;
    private User booker;
    private User outsider;
    private Item item;

    @BeforeEach
    void prepare() {
        owner = user();
        booker = user();
        outsider = user();
        item = new Item();
        item.setName("Drill");
        item.setDescription("Tool");
        item.setAvailable(true);
        item.setOwner(owner);
        item = items.save(item);
    }

    @Test
    void bookingLifecycleAndAccess() {
        BookingDto dto = new BookingDto();
        dto.setItemId(item.getId());
        dto.setStart(LocalDateTime.now().plusDays(1));
        dto.setEnd(LocalDateTime.now().plusDays(2));
        var created = bookings.create(booker.getId(), dto);
        assertThat(created.status()).isEqualTo(BookingStatus.WAITING);
        assertThat(bookings.get(owner.getId(), created.id()).id()).isEqualTo(created.id());
        assertThatThrownBy(() -> bookings.get(outsider.getId(), created.id()))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> bookings.approve(booker.getId(), created.id(), true))
                .isInstanceOf(ForbiddenException.class);
        assertThat(bookings.approve(owner.getId(), created.id(), true).status())
                .isEqualTo(BookingStatus.APPROVED);
        assertThatThrownBy(() -> bookings.approve(owner.getId(), created.id(), true))
                .isInstanceOf(BadRequestException.class);
        assertThatThrownBy(() -> bookings.create(owner.getId(), dto)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void filtersAndDescendingOrderForBookerAndOwner() {
        LocalDateTime now = LocalDateTime.now();
        Booking past = seed(now.minusDays(3), now.minusDays(2), BookingStatus.APPROVED);
        Booking current = seed(now.minusHours(1), now.plusHours(1), BookingStatus.APPROVED);
        Booking waiting = seed(now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
        Booking rejected = seed(now.plusDays(3), now.plusDays(4), BookingStatus.REJECTED);
        for (boolean ownerView : List.of(false, true)) {
            long id = ownerView ? owner.getId() : booker.getId();
            assertThat(bookings.getAll(id, "ALL", ownerView)).extracting(b -> b.id())
                    .containsExactly(rejected.getId(), waiting.getId(), current.getId(), past.getId());
            assertThat(bookings.getAll(id, "PAST", ownerView)).extracting(b -> b.id())
                    .containsExactly(past.getId());
            assertThat(bookings.getAll(id, "CURRENT", ownerView)).extracting(b -> b.id())
                    .containsExactly(current.getId());
            assertThat(bookings.getAll(id, "FUTURE", ownerView)).extracting(b -> b.id())
                    .containsExactly(rejected.getId(), waiting.getId());
            assertThat(bookings.getAll(id, "WAITING", ownerView)).extracting(b -> b.id())
                    .containsExactly(waiting.getId());
            assertThat(bookings.getAll(id, "REJECTED", ownerView)).extracting(b -> b.id())
                    .containsExactly(rejected.getId());
        }
    }

    @Test
    void nearestApprovedBookingsOnlyVisibleToOwner() {
        LocalDateTime now = LocalDateTime.now();
        seed(now.minusDays(5), now.minusDays(4), BookingStatus.APPROVED);
        Booking last = seed(now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);
        Booking next = seed(now.plusDays(1), now.plusDays(2), BookingStatus.APPROVED);
        seed(now.plusDays(3), now.plusDays(4), BookingStatus.APPROVED);
        seed(now.plusHours(1), now.plusHours(2), BookingStatus.WAITING);
        var ownView = itemService.get(item.getId(), owner.getId());
        assertThat(ownView.getLastBooking().id()).isEqualTo(last.getId());
        assertThat(ownView.getNextBooking().id()).isEqualTo(next.getId());
        assertThat(itemService.getAll(owner.getId()).getFirst().getNextBooking().id()).isEqualTo(next.getId());
        assertThat(itemService.get(item.getId(), booker.getId()).getLastBooking()).isNull();
        assertThat(itemService.get(item.getId(), null).getNextBooking()).isNull();
    }

    @Test
    void commentsRequireCompletedApprovedBooking() {
        CommentDto dto = new CommentDto();
        dto.setText("Useful tool");
        assertThatThrownBy(() -> itemService.addComment(booker.getId(), item.getId(), dto))
                .isInstanceOf(BadRequestException.class);
        LocalDateTime now = LocalDateTime.now();
        seed(now.minusDays(2), now.minusDays(1), BookingStatus.REJECTED);
        assertThatThrownBy(() -> itemService.addComment(booker.getId(), item.getId(), dto))
                .isInstanceOf(BadRequestException.class);
        seed(now.minusDays(2), now.minusDays(1), BookingStatus.APPROVED);
        var comment = itemService.addComment(booker.getId(), item.getId(), dto);
        assertThat(comment.getAuthorName()).isEqualTo(booker.getName());
        assertThat(itemService.get(item.getId(), outsider.getId()).getComments())
                .extracting(CommentDto::getText).containsExactly("Useful tool");
        assertThat(itemService.getAll(owner.getId()).getFirst().getComments()).hasSize(1);
    }

    @Test
    void invalidRequestsAndUnknownStatesReturnBadRequest() throws Exception {
        mvc.perform(get("/bookings").header("X-Sharer-User-Id", booker.getId()).param("state", "UNKNOWN"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/bookings").header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"itemId\":1}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/items/{id}/comment", item.getId()).header("X-Sharer-User-Id", booker.getId())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"  \"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void managedUpdatesPersistWithoutExplicitSave() {
        LocalDateTime now = LocalDateTime.now();
        Booking booking = seed(now.plusDays(1), now.plusDays(2), BookingStatus.WAITING);
        bookings.approve(owner.getId(), booking.getId(), true);
        ItemDto patch = new ItemDto();
        patch.setName("Updated drill");
        patch.setAvailable(false);
        itemService.update(owner.getId(), item.getId(), patch);
        entityManager.flush();
        entityManager.clear();
        assertThat(bookingRepository.findById(booking.getId()).orElseThrow().getStatus())
                .isEqualTo(BookingStatus.APPROVED);
        Item reloaded = items.findById(item.getId()).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo("Updated drill");
        assertThat(reloaded.getAvailable()).isFalse();
        assertThat(reloaded.getOwner().getId()).isEqualTo(owner.getId());
    }

    private User user() {
        User user = new User();
        user.setName("User");
        user.setEmail(UUID.randomUUID() + "@example.org");
        return users.save(user);
    }

    private Booking seed(LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStart(start);
        booking.setEnd(end);
        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}
