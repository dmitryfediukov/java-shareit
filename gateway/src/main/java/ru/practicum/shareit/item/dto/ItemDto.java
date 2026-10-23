package ru.practicum.shareit.item.dto;

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

    @jakarta.validation.constraints.Positive
    private Long requestId;
}
