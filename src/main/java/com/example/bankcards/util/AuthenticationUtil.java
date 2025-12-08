package com.example.bankcards.util;

import com.example.bankcards.exception.AuthenticationException;
import com.example.bankcards.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;

import java.util.function.Function;

@Slf4j
public class AuthenticationUtil {
    public static <T> T withAuthentication(Authentication authentication, Function<UserDetailsImpl, T> function) throws AuthenticationException.NotAuthenticated {
        if (authentication != null && authentication.isAuthenticated()) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            return function.apply(userDetails);
        }
        throw new AuthenticationException.NotAuthenticated();
    }
}
