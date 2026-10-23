package ru.practicum.shareit.item.dto;

import java.util.List;
import java.util.ArrayList;
import ru.practicum.shareit.booking.dto.BookingSummaryDto;
import lombok.Data;

@Data
public class ItemDto {
    private Long id;

    private String name;

    private String description;

    private Boolean available;
    private BookingSummaryDto lastBooking;
    private BookingSummaryDto nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
    private Long requestId;
}
