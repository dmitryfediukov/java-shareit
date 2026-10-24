package ru.practicum.shareit;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.booking.dto.BookingState;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest({UserController.class, ItemController.class, BookingController.class, ItemRequestController.class})
class ControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserClient users;

    @MockBean
    private ItemClient items;

    @MockBean
    private BookingClient bookings;

    @MockBean
    private ItemRequestClient requests;

    @Test
    void userCreate() throws Exception {
        when(users.create(any())).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(post("/users").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Ada\",\"email\":\"ada@example.org\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(users).create(any());
    }

    @Test
    void userUpdate() throws Exception {
        when(users.update(eq(1L), any())).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(patch("/users/1").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Ada\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(users).update(eq(1L), any());
    }

    @Test
    void userGet() throws Exception {
        when(users.getOne(1L)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/users/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(users).getOne(1L);
    }

    @Test
    void userList() throws Exception {
        when(users.getAll()).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/users").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(users).getAll();
    }

    @Test
    void userDelete() throws Exception {
        when(users.remove(1L)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(delete("/users/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(users).remove(1L);
    }

    @Test
    void itemCreate() throws Exception {
        when(items.create(eq(1L), any())).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(post("/items").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Drill\",\"description\":\"Tool\",\"available\":true,\"requestId\":2}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(items).create(eq(1L), any());
    }

    @Test
    void itemUpdate() throws Exception {
        when(items.update(eq(1L), eq(2L), any())).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(patch("/items/2").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"available\":false}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(items).update(eq(1L), eq(2L), any());
    }

    @Test
    void itemGet() throws Exception {
        when(items.getOne(1L, 2L)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/items/2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(items).getOne(1L, 2L);
    }

    @Test
    void itemList() throws Exception {
        when(items.getAll(1L)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(items).getAll(1L);
    }

    @Test
    void itemSearch() throws Exception {
        when(items.search(1L, "drill")).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/items/search?text=drill").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(items).search(1L, "drill");
    }

    @Test
    void itemComment() throws Exception {
        when(items.comment(eq(1L), eq(2L), any())).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(post("/items/2/comment").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"Good\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(items).comment(eq(1L), eq(2L), any());
    }

    @Test
    void bookingCreate() throws Exception {
        when(bookings.bookItem(eq(1L), any())).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"itemId\":2,\"start\":\"2099-01-01T10:00:00\",\"end\":\"2099-01-02T10:00:00\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(bookings).bookItem(eq(1L), any());
    }

    @Test
    void bookingApprove() throws Exception {
        when(bookings.approve(1L, 2L, true)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(patch("/bookings/2?approved=true").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(bookings).approve(1L, 2L, true);
    }

    @Test
    void bookingGet() throws Exception {
        when(bookings.getBooking(1L, 2L)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/bookings/2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(bookings).getBooking(1L, 2L);
    }

    @Test
    void bookingList() throws Exception {
        when(bookings.getBookings(1L, BookingState.ALL, 0, 10, false)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/bookings?state=ALL").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(bookings).getBookings(1L, BookingState.ALL, 0, 10, false);
    }

    @Test
    void bookingOwner() throws Exception {
        when(bookings.getBookings(1L, BookingState.ALL, 0, 10, true)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/bookings/owner?state=ALL").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(bookings).getBookings(1L, BookingState.ALL, 0, 10, true);
    }

    @Test
    void requestCreate() throws Exception {
        when(requests.create(eq(1L), any())).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(post("/requests").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"description\":\"Need a drill\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(requests).create(eq(1L), any());
    }

    @Test
    void requestOwn() throws Exception {
        when(requests.getOwn(1L)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(requests).getOwn(1L);
    }

    @Test
    void requestAll() throws Exception {
        when(requests.getAll(1L, 3, 2)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/requests/all?from=3&size=2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(requests).getAll(1L, 3, 2);
    }

    @Test
    void requestGet() throws Exception {
        when(requests.getOne(1L, 2L)).thenReturn(ResponseEntity.ok(Map.of("ok", true)));
        mvc.perform(get("/requests/2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk()).andExpect(jsonPath("$.ok").value(true));
        verify(requests).getOne(1L, 2L);
    }

    @Test
    void invalidInputsNeverReachClients() throws Exception {
        for (String body : List.of("{}", "{\"name\":\"Ada\",\"email\":\"invalid\"}")) {
            mvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON).content(body))
                    .andExpect(status().isBadRequest());
        }
        mvc.perform(post("/items").header("X-Sharer-User-Id", 1).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\" \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/requests").header("X-Sharer-User-Id", 1).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"description\":\"  \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 1).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":2,\"start\":\"2099-02-02T00:00:00\",\"end\":\"2099-01-01T00:00:00\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/items/2/comment").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\" \"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/requests/all?from=-1&size=2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
        mvc.perform(get("/bookings?state=UNKNOWN").header("X-Sharer-User-Id", 1))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(users, items, bookings, requests);
    }

}
