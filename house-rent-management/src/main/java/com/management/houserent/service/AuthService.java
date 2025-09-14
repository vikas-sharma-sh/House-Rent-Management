package com.management.houserent.service;

import com.management.houserent.dto.RegisterRequest;
import com.management.houserent.dto.AuthRequest;
import com.management.houserent.dto.AuthResponse;
import com.management.houserent.dto.auth.LoginRequest;

public interface AuthService {
    AuthResponse register(RegisterRequest req);
    AuthResponse login(LoginRequest req);
}
