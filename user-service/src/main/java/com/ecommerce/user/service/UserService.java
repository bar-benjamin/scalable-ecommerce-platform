package com.ecommerce.user.service;

import com.ecommerce.user.domain.User;
import com.ecommerce.user.dto.*;
import com.ecommerce.user.exception.InvalidCredentialsException;
import com.ecommerce.user.exception.UserAlreadyExistsException;
import com.ecommerce.user.exception.UserNotFoundException;
import com.ecommerce.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository user_repository;
    private final PasswordEncoder password_encoder;
    private final JwtService jwt_service;

    public UserService(UserRepository user_repository,
                       PasswordEncoder password_encoder,
                       JwtService jwt_service) {
        this.user_repository  = user_repository;
        this.password_encoder = password_encoder;
        this.jwt_service      = jwt_service;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (user_repository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(password_encoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .role(User.Role.CUSTOMER)
                .build();

        User saved = user_repository.save(user);
        String token = jwt_service.generateToken(saved);

        return new AuthResponse(token, jwt_service.getExpirationMs(), UserResponse.from(saved));
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = user_repository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException());

        if (!password_encoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String token = jwt_service.generateToken(user);

        return new AuthResponse(token, jwt_service.getExpirationMs(), UserResponse.from(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(Long user_id) {
        User user = user_repository.findById(user_id)
                .orElseThrow(() -> new UserNotFoundException(user_id));

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(Long user_id, UpdateProfileRequest request) {
        User user = user_repository.findById(user_id)
                .orElseThrow(() -> new UserNotFoundException(user_id));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());

        return UserResponse.from(user_repository.save(user));
    }
}