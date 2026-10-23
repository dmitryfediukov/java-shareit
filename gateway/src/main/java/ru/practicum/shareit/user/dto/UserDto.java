package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import ru.practicum.shareit.validation.Create;

@Data
public class UserDto {
    private Long id;

    @NotBlank(groups = Create.class)
    @Pattern(regexp = "(?s).*\\S.*", message = "Name must not be blank")
    private String name;

    @NotBlank(groups = Create.class)
    @Email
    @Pattern(regexp = "(?s).*\\S.*", message = "Email must not be blank")
    private String email;
}
