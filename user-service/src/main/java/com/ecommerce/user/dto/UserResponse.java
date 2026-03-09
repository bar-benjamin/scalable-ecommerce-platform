package com.ecommerce.user.dto;

import com.ecommerce.user.domain.User;

import java.time.Instant;

public class UserResponse {

    private final Long id;
    private final String email;
    private final String first_name;
    private final String last_name;
    private final String role;
    private final Instant created_at;
    private final Instant updated_at;

    private UserResponse(Long id, String email, String first_name,
                         String last_name, String role,
                         Instant created_at, Instant updated_at) {
        this.id         = id;
        this.email      = email;
        this.first_name = first_name;
        this.last_name  = last_name;
        this.role       = role;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public Long getId()          { return id; }
    public String getEmail()     { return email; }
    public String getFirstName() { return first_name; }
    public String getLastName()  { return last_name; }
    public String getRole()      { return role; }
    public Instant getCreatedAt(){ return created_at; }
    public Instant getUpdatedAt(){ return updated_at; }
}