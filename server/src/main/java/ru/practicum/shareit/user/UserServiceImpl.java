package ru.practicum.shareit.user;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        checkEmail(dto.getEmail(), null);
        User user = UserMapper.toModel(dto);
        user.setId(null);
        return UserMapper.toDto(repository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(long id, UserDto dto) {
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
        return repository.findAll(Sort.by("id")).stream().map(UserMapper::toDto).toList();
    }

    @Override
    @Transactional
    public void delete(long id) {
        requireUser(id);
        repository.deleteById(id);
    }

    private User requireUser(long id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    private void checkEmail(String email, Long excludedId) {
        boolean duplicate = excludedId == null ? repository.existsByEmailIgnoreCase(email)
                : repository.existsByEmailIgnoreCaseAndIdNot(email, excludedId);
        if (duplicate) {
            throw new ConflictException("Email already exists: " + email);
        }
    }
}
