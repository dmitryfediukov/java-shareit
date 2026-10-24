package ru.practicum.shareit.user;

import java.util.List;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {
    UserDto create(UserDto dto);

    UserDto update(long id, UserDto dto);

    UserDto get(long id);

    List<UserDto> getAll();

    void delete(long id);
}
