package com.ecommerce.user.controller;

import com.ecommerce.user.dto.AuthResponse;
import com.ecommerce.user.dto.LoginRequest;
import com.ecommerce.user.dto.RegisterRequest;
import com.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * A REST controller exposing two endpoints - /api/auth/register and /api/auth/login. Both accept a JSON body,
 * delegate all the actual work to UserService, and return an AuthResponse with a relevant status code.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService user_service;

    public AuthController(UserService user_service) {
        this.user_service = user_service;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = user_service.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(user_service.login(request));
    }
}