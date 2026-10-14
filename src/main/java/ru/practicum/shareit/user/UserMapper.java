package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public final class UserMapper {

    private UserMapper() {
    }

    public static UserDto toDto(User model) {
        UserDto dto = new UserDto();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setEmail(model.getEmail());
        return dto;
    }

    public static User toModel(UserDto dto) {
        User model = new User();
        model.setId(dto.getId());
        model.setName(dto.getName());
        model.setEmail(dto.getEmail());
        return model;
    }
}
