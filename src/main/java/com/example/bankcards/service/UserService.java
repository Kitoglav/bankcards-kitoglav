package com.example.bankcards.service;

import com.example.bankcards.entity.impl.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User findUser(final String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User %s not found".formatted(username)));
    }
}
