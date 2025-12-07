package com.example.bankcards.security;

import com.example.bankcards.entity.impl.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collections;
import java.util.Set;

@Getter
public class UserDetailsImpl implements UserDetails {
    private final Long id;
    private final String username;
    private final String password;
    private final Set<GrantedAuthority> authorities;

    public UserDetailsImpl(final Long id, final String username, final String password, final Set<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserDetailsImpl of(final User user) {
        return new UserDetailsImpl(user.getId(), user.getUsername(), user.getPasswordHash(), Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole())));
    }
}
