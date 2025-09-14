package com.management.houserent.service;

import com.management.houserent.dto.RegisterRequest;
import com.management.houserent.dto.AuthRequest;
import com.management.houserent.dto.AuthResponse;
import com.management.houserent.dto.auth.LoginRequest;
import com.management.houserent.exception.DuplicateResourceException;
import com.management.houserent.model.*;
import com.management.houserent.repository.*;
import com.management.houserent.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authManager;



    private final UserRepository userRepo;
    private final OwnerRepository ownerRepo;
    private final TenantRepository tenantRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthServiceImpl(
            AuthenticationManager authManager,
            UserRepository userRepo,
            OwnerRepository ownerRepo,
            TenantRepository tenantRepo,
            PasswordEncoder encoder,
            JwtService jwtService) {
        this.authManager = authManager;
        this.userRepo = userRepo;
        this.ownerRepo = ownerRepo;
        this.tenantRepo = tenantRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {

        String email = req.getEmail().toLowerCase().trim();

        if (userRepo.existsByEmail(email)) {
            throw new DuplicateResourceException("User with email already exists!"+ email);
        }


        if ("ROLE_OWNER".equalsIgnoreCase(req.getRole()) && ownerRepo.existsByEmail(email)) {
            throw new DuplicateResourceException("Owner with email already exists: " );
        }
        if ("ROLE_TENANT".equalsIgnoreCase(req.getRole()) && tenantRepo.existsByEmail(email)) {
            throw new DuplicateResourceException("Tenant with email already exists: " );
        }

        Role role = Role.valueOf(req.getRole().trim().toUpperCase());

        User user = new User();
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(role);
        userRepo.save(user);

        if (role == Role.ROLE_OWNER) {
            Owner owner = new Owner();
            owner.setName(req.getName());
            owner.setEmail(req.getEmail());
            owner.setPhone(req.getPhone());
            owner.setUser(user);
            ownerRepo.save(owner);
        } else if (role == Role.ROLE_TENANT) {
            Tenant tenant = new Tenant();
            tenant.setName(req.getName());
            tenant.setEmail(req.getEmail());
            tenant.setPhone(req.getPhone());
            tenant.setUser(user);
            tenantRepo.save(tenant);
        }

        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(auth);
        userRepo.findByEmail(req.getEmail()).orElseThrow();
        String token = jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name()));
        return new AuthResponse(token, user.getRole().name());
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));
        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow();
        String token = jwtService.generateToken(user.getEmail(), Map.of("role", user.getRole().name()));
        return new AuthResponse(token, user.getRole().name());
    }
}
