package com.example.bankcards.security;

import com.example.bankcards.entity.impl.User;
import com.example.bankcards.exception.EntityNotFoundException;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserService userService;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        try {
            User user = userService.getUser(username);
            return UserDetailsImpl.of(user);
        } catch (EntityNotFoundException e) {
            log.error("User not found {}", username, e);
            throw new UsernameNotFoundException("User %s not found".formatted(username));
        } catch (Exception e) {
            log.error("Failed to load user: {}", username, e);
            throw new UsernameNotFoundException("Failed to load user %s".formatted(username), e);
        }
    }
}
