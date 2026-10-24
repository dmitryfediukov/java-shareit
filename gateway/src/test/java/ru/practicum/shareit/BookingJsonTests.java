package ru.practicum.shareit;

import java.time.LocalDateTime;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingJsonTests {
    @Autowired
    private JacksonTester<BookItemRequestDto> json;

    @Test
    void roundTripDatesAndHideValidationProperty() throws Exception {
        BookItemRequestDto dto = new BookItemRequestDto();
        dto.setItemId(42L);
        dto.setStart(LocalDateTime.of(2099, 1, 1, 12, 0));
        dto.setEnd(dto.getStart().plusHours(1));
        var content = json.write(dto);
        assertThat(content).extractingJsonPathNumberValue("$.itemId").isEqualTo(42);
        assertThat(content).doesNotHaveJsonPath("$.dateRangeValid");
        assertThat(json.parseObject(content.getJson())).isEqualTo(dto);
    }

    @Test
    void missingPastAndReversedDatesAreRejected() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            BookItemRequestDto dto = new BookItemRequestDto();
            assertThat(validator.validate(dto)).hasSize(3);
            dto.setItemId(1L);
            dto.setStart(LocalDateTime.now().minusDays(2));
            dto.setEnd(LocalDateTime.now().minusDays(1));
            assertThat(validator.validate(dto)).hasSize(2);
            dto.setStart(LocalDateTime.now().plusDays(3));
            dto.setEnd(LocalDateTime.now().plusDays(2));
            assertThat(validator.validate(dto)).extracting(v -> v.getPropertyPath().toString())
                    .containsExactly("dateRangeValid");
        }
    }
}
