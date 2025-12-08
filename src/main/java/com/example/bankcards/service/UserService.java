package com.example.bankcards.service;

import com.example.bankcards.dto.admin.CreateUserRequest;
import com.example.bankcards.entity.Role;
import com.example.bankcards.entity.impl.User;
import com.example.bankcards.exception.EntityAlreadyExistsException;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private User findUser(final String username) throws EntityNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User %s not found".formatted(username)));
    }

    private User findUser(final Long id) throws EntityNotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("User with id %s not found".formatted(id)));
    }

    @Transactional
    public User deleteUser(final Long userId) {
        User user = findUser(userId);
        userRepository.delete(user);
        return user;
    }

    @Transactional
    public User createNewUser(final CreateUserRequest request) throws EntityAlreadyExistsException {
        if (userRepository.existsByUsername(request.username())) {
            throw new EntityAlreadyExistsException("User %s already exists".formatted(request.username()));
        }
        User user = new User();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUser(Long userId) {
        return findUser(userId);
    }

    @Transactional(readOnly = true)
    public User getUser(String username) {
        return findUser(username);
    }

    @Transactional(readOnly = true)
    public Page<User> getUsers(final Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
