package ru.practicum.shareit;


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
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({UserController.class, ItemController.class, BookingController.class, ItemRequestController.class})
class ControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService users;

    @MockBean
    private ItemService items;

    @MockBean
    private BookingService bookings;

    @MockBean
    private ItemRequestService requests;

    @Test
    void userCreate() throws Exception {
        when(users.create(any())).thenReturn(new UserDto());
        mvc.perform(post("/users").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Ada\",\"email\":\"ada@example.org\"}"))
                .andExpect(status().isOk());
        verify(users).create(any());
    }

    @Test
    void userUpdate() throws Exception {
        when(users.update(eq(1L), any())).thenReturn(new UserDto());
        mvc.perform(patch("/users/1").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Ada\"}"))
                .andExpect(status().isOk());
        verify(users).update(eq(1L), any());
    }

    @Test
    void userGet() throws Exception {
        when(users.get(1L)).thenReturn(new UserDto());
        mvc.perform(get("/users/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(users).get(1L);
    }

    @Test
    void userList() throws Exception {
        when(users.getAll()).thenReturn(List.of());
        mvc.perform(get("/users").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(users).getAll();
    }

    @Test
    void userDelete() throws Exception {
        mvc.perform(delete("/users/1").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(users).delete(1L);
    }

    @Test
    void itemCreate() throws Exception {
        when(items.create(eq(1L), any())).thenReturn(new ItemDto());
        mvc.perform(post("/items").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"name\":\"Drill\",\"description\":\"Tool\",\"available\":true,\"requestId\":2}"))
                .andExpect(status().isOk());
        verify(items).create(eq(1L), any());
    }

    @Test
    void itemUpdate() throws Exception {
        when(items.update(eq(1L), eq(2L), any())).thenReturn(new ItemDto());
        mvc.perform(patch("/items/2").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"available\":false}"))
                .andExpect(status().isOk());
        verify(items).update(eq(1L), eq(2L), any());
    }

    @Test
    void itemGet() throws Exception {
        when(items.get(2L, 1L)).thenReturn(new ItemDto());
        mvc.perform(get("/items/2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(items).get(2L, 1L);
    }

    @Test
    void itemList() throws Exception {
        when(items.getAll(1L)).thenReturn(List.of());
        mvc.perform(get("/items").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(items).getAll(1L);
    }

    @Test
    void itemSearch() throws Exception {
        when(items.search("drill")).thenReturn(List.of());
        mvc.perform(get("/items/search?text=drill").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(items).search("drill");
    }

    @Test
    void itemComment() throws Exception {
        when(items.addComment(eq(1L), eq(2L), any())).thenReturn(new CommentDto());
        mvc.perform(post("/items/2/comment").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"text\":\"Good\"}"))
                .andExpect(status().isOk());
        verify(items).addComment(eq(1L), eq(2L), any());
    }

    @Test
    void bookingCreate() throws Exception {
        when(bookings.create(eq(1L), any())).thenReturn(null);
        mvc.perform(post("/bookings").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"itemId\":2,\"start\":\"2099-01-01T10:00:00\",\"end\":\"2099-01-02T10:00:00\"}"))
                .andExpect(status().isOk());
        verify(bookings).create(eq(1L), any());
    }

    @Test
    void bookingApprove() throws Exception {
        when(bookings.approve(1L, 2L, true)).thenReturn(null);
        mvc.perform(patch("/bookings/2?approved=true").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(bookings).approve(1L, 2L, true);
    }

    @Test
    void bookingGet() throws Exception {
        when(bookings.get(1L, 2L)).thenReturn(null);
        mvc.perform(get("/bookings/2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(bookings).get(1L, 2L);
    }

    @Test
    void bookingList() throws Exception {
        when(bookings.getAll(1L, "ALL", false, 0, 10)).thenReturn(List.of());
        mvc.perform(get("/bookings?state=ALL").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(bookings).getAll(1L, "ALL", false, 0, 10);
    }

    @Test
    void bookingOwner() throws Exception {
        when(bookings.getAll(1L, "ALL", true, 0, 10)).thenReturn(List.of());
        mvc.perform(get("/bookings/owner?state=ALL").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(bookings).getAll(1L, "ALL", true, 0, 10);
    }

    @Test
    void requestCreate() throws Exception {
        when(requests.create(eq(1L), any())).thenReturn(new ItemRequestDto());
        mvc.perform(post("/requests").header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON).content("{\"description\":\"Need a drill\"}"))
                .andExpect(status().isOk());
        verify(requests).create(eq(1L), any());
    }

    @Test
    void requestOwn() throws Exception {
        when(requests.getOwn(1L)).thenReturn(List.of());
        mvc.perform(get("/requests").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(requests).getOwn(1L);
    }

    @Test
    void requestAll() throws Exception {
        when(requests.getAll(1L, 3, 2)).thenReturn(List.of());
        mvc.perform(get("/requests/all?from=3&size=2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(requests).getAll(1L, 3, 2);
    }

    @Test
    void requestGet() throws Exception {
        when(requests.get(1L, 2L)).thenReturn(new ItemRequestDto());
        mvc.perform(get("/requests/2").header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
        verify(requests).get(1L, 2L);
    }

}
