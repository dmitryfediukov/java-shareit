package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BookItemRequestDto {
    @NotNull
    @Positive
    private Long itemId;

    @NotNull
    @Future
    private LocalDateTime start;

    @NotNull
    @Future
    private LocalDateTime end;

    @JsonIgnore
    @AssertTrue(message = "End must be after start")
    public boolean isDateRangeValid() {
        return start == null || end == null || end.isAfter(start);
    }
}
