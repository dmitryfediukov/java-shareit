package ru.practicum.shareit.booking;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

@Service
public class BookingClient extends BaseClient {
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(builder.uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                .requestFactory(() -> new JdkClientHttpRequestFactory()).build());
    }

    public ResponseEntity<Object> bookItem(long userId, BookItemRequestDto dto) {
        return post("", userId, dto);
    }

    public ResponseEntity<Object> getBooking(long userId, long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> approve(long userId, long id, boolean approved) {
        return patch("/" + id + "?approved={approved}", userId, Map.of("approved", approved), null);
    }

    public ResponseEntity<Object> getBookings(long userId, BookingState state, int from, int size, boolean owner) {
        return get((owner ? "/owner" : "") + "?state={state}&from={from}&size={size}", userId,
                Map.of("state", state.name(), "from", from, "size", size));
    }
}
