package ru.practicum.shareit.item.dto;

import java.util.List;
import java.util.ArrayList;
import ru.practicum.shareit.booking.dto.BookingSummaryDto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.practicum.shareit.validation.Create;

@Data
public class ItemDto {
    private Long id;

    @NotBlank(groups = Create.class)
    @Pattern(regexp = "(?s).*\\S.*", message = "Name must not be blank")
    private String name;

    @NotBlank(groups = Create.class)
    @Pattern(regexp = "(?s).*\\S.*", message = "Description must not be blank")
    private String description;

    @NotNull(groups = Create.class)
    private Boolean available;
    private BookingSummaryDto lastBooking;
    private BookingSummaryDto nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
