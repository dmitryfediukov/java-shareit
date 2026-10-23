package ru.practicum.shareit;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class RequestTests {
    @Autowired
    private ItemRequestService requests;

    @Autowired
    private ItemRequestRepository repository;

    @Autowired
    private UserRepository users;

    @Autowired
    private ItemService items;

    private User author;
    private User responder;

    @BeforeEach
    void prepare() {
        author = user();
        responder = user();
    }

    @Test
    void createAndReadRequestWithResponseFromAnotherOwner() {
        ItemRequestDto input = new ItemRequestDto();
        input.setDescription("Need drill");
        var request = requests.create(author.getId(), input);
        assertThat(request.getCreated()).isNotNull();
        assertThat(request.getItems()).isEmpty();
        ItemDto item = item();
        item.setRequestId(request.getId());
        var response = items.create(responder.getId(), item);
        assertThat(response.getRequestId()).isEqualTo(request.getId());
        var fromOtherUser = requests.get(responder.getId(), request.getId());
        assertThat(fromOtherUser.getItems()).hasSize(1);
        assertThat(fromOtherUser.getItems().getFirst().ownerId()).isEqualTo(responder.getId());
        assertThat(requests.getOwn(author.getId()).getFirst().getItems().getFirst().id())
                .isEqualTo(response.getId());
        assertThat(requests.getAll(responder.getId(), 0, 10)).hasSize(1);
    }

    @Test
    void listsExcludeOwnRequestsAndUseDescendingDatesWithExactOffset() {
        for (int i = 0; i < 5; i++) {
            ItemRequest request = new ItemRequest();
            request.setDescription("Request " + i);
            request.setRequester(i == 4 ? responder : author);
            request.setCreated(LocalDateTime.of(2020, 1, 1, 0, 0).plusHours(i));
            repository.save(request);
        }
        assertThat(requests.getOwn(author.getId())).extracting(ItemRequestDto::getDescription)
                .containsExactly("Request 3", "Request 2", "Request 1", "Request 0");
        assertThat(requests.getAll(responder.getId(), 1, 2)).extracting(ItemRequestDto::getDescription)
                .containsExactly("Request 2", "Request 1");
        assertThat(requests.getAll(responder.getId(), 9, 2)).isEmpty();
    }

    @Test
    void missingUsersRequestsAndOptionalRequestLink() {
        assertThat(items.create(author.getId(), item()).getRequestId()).isNull();
        assertThatThrownBy(() -> requests.get(author.getId(), Long.MAX_VALUE))
                .isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> requests.getOwn(Long.MAX_VALUE)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> requests.getAll(Long.MAX_VALUE, 0, 10)).isInstanceOf(NotFoundException.class);
        ItemRequestDto input = new ItemRequestDto();
        input.setDescription("Need drill");
        assertThatThrownBy(() -> requests.create(Long.MAX_VALUE, input)).isInstanceOf(NotFoundException.class);
        ItemDto item = item();
        item.setRequestId(Long.MAX_VALUE);
        assertThatThrownBy(() -> items.create(author.getId(), item)).isInstanceOf(NotFoundException.class);
    }

    private ItemDto item() {
        ItemDto dto = new ItemDto();
        dto.setName("Drill");
        dto.setDescription("Tool");
        dto.setAvailable(true);
        return dto;
    }

    private User user() {
        User user = new User();
        user.setName("User");
        user.setEmail(UUID.randomUUID() + "@example.org");
        return users.save(user);
    }
}
