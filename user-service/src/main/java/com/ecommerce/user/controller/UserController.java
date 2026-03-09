package com.ecommerce.user.controller;

import com.ecommerce.user.dto.UpdateProfileRequest;
import com.ecommerce.user.dto.UserResponse;
import com.ecommerce.user.exception.ForbiddenException;
import com.ecommerce.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for /api/users with two endpoints - get and update a user profile.
 * Both read the X-User-Id and X-User-Role headers forwarded by the gateway to enforce authorization:
 * Users can only access their own profile, ADMINs can fetch anyone's.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService user_service;

    public UserController(UserService user_service) {
        this.user_service = user_service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getProfile(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String requesting_user_id,
            @RequestHeader("X-User-Role") String requesting_user_role) {

        if (!requesting_user_role.equals("ADMIN") && !requesting_user_id.equals(id.toString())) {
            throw new ForbiddenException("You can only view your own profile");
        }

        return ResponseEntity.ok(user_service.getProfile(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request,
            @RequestHeader("X-User-Id") String requesting_user_id) {

        if (!requesting_user_id.equals(id.toString())) {
            throw new ForbiddenException("You can only update your own profile");
        }

        return ResponseEntity.ok(user_service.updateProfile(id, request));
    }
}