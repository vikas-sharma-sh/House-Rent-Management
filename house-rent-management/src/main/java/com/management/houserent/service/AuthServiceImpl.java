package com.management.houserent.service;

import com.management.houserent.dto.AuthRequest;
import com.management.houserent.dto.AuthResponse;
import com.management.houserent.dto.RegisterRequest;
import com.management.houserent.exception.DuplicateResourceException;
import com.management.houserent.model.User;
import com.management.houserent.repository.UserRepository;
import com.management.houserent.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepo;
    private final PasswordEncoder encoder ;
    private final AuthenticationManager authManager;
    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepo, PasswordEncoder encoder, AuthenticationManager authManager, JwtService jwtService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if(userRepo.existsByEmail(request.getEmail().toLowerCase().trim())){
            throw new DuplicateResourceException("User with email already exists : "+ request.getEmail() );
        }

        User u = new User();
        u.setEmail(request.getEmail());
        u.setPassword(encoder.encode(request.getPassword()));
        u.setRole(request.getRole());
        User saved = userRepo.save(u);

        String token = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        saved.getEmail(),
                        saved.getPassword(),
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                saved.getRole().name()
                        )
                )

        ));

        return  new AuthResponse(token ,saved.getRole().name());
    }

    @Override
    public AuthResponse login(AuthRequest request) {

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword());
        authManager.authenticate(token);

        var user = userRepo.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(()->new BadCredentialsException("Invalid credentials"));

        String jwt = jwtService.generateToken(
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),user.getPassword(),
                        java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(
                                user.getRole().name()
                        ) )
                )
        );
        return new AuthResponse(jwt,user.getRole().name());
    }
}
