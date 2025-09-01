package com.management.houserent.service;

import com.management.houserent.dto.AuthRequest;
import com.management.houserent.dto.AuthResponse;
import com.management.houserent.dto.RegisterRequest;

public interface AuthService {

    AuthResponse register(RegisterRequest request);//AuthResponse register()
    AuthResponse login(AuthRequest request);
}
