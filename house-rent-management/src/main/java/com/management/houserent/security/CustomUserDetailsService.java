package com.management.houserent.security;

import com.management.houserent.model.User;
import com.management.houserent.repository.UserRepository;

import java.util.Optional;

import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repo;

    public CustomUserDetailsService(UserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> opt = repo.findByEmail(username);
        User u = opt.orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // UserDetails needed only at login (we embed role in JWT later)
        return org.springframework.security.core.userdetails.User
                .withUsername(u.getEmail())
                .password(u.getPassword())
                .roles(u.getRole().name().replace("ROLE_", "")) // Spring adds ROLE_ prefix automatically
                .build();
    }
}
