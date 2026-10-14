package ru.practicum.shareit.user;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public synchronized UserDto create(UserDto dto) {
        checkEmail(dto.getEmail(), null);
        User user = UserMapper.toModel(dto);
        user.setId(null);
        return UserMapper.toDto(repository.save(user));
    }

    @Override
    public synchronized UserDto update(long id, UserDto dto) {
        User user = requireUser(id);
        if (dto.getEmail() != null) {
            checkEmail(dto.getEmail(), id);
            user.setEmail(dto.getEmail());
        }
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        return UserMapper.toDto(repository.save(user));
    }

    @Override
    public UserDto get(long id) {
        return UserMapper.toDto(requireUser(id));
    }

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream().map(UserMapper::toDto).toList();
    }

    @Override
    public synchronized void delete(long id) {
        requireUser(id);
        repository.delete(id);
    }

    private User requireUser(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private void checkEmail(String email, Long excludedId) {
        boolean duplicate = repository.findAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email) && !user.getId().equals(excludedId));
        if (duplicate) {
            throw new ConflictException("Email already exists: " + email);
        }
    }
}
