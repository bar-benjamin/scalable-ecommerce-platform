package com.ecommerce.user.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "users",
        schema = "user_schema",
        uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password_hash;

    @Column(nullable = false)
    private String first_name;

    @Column(nullable = false)
    private String last_name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private Instant created_at;

    @Column(nullable = false)
    private Instant updated_at;

    public enum Role {
        CUSTOMER, ADMIN
    }

    // Default constructor required by JPA
    protected User() {}

    private User(Builder builder) {
        this.email = builder.email;
        this.password_hash = builder.password_hash;
        this.first_name = builder.first_name;
        this.last_name = builder.last_name;
        this.role = builder.role;
        this.created_at = Instant.now();
        this.updated_at = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updated_at = Instant.now();
    }

    public Long getId()            { return id; }
    public String getEmail()       { return email; }
    public String getPasswordHash(){ return password_hash; }
    public String getFirstName()   { return first_name; }
    public String getLastName()    { return last_name; }
    public Role getRole()          { return role; }
    public Instant getCreatedAt()  { return created_at; }
    public Instant getUpdatedAt()  { return updated_at; }


    public void setFirstName(String first_name)   { this.first_name = first_name; }
    public void setLastName(String last_name)     { this.last_name = last_name; }
    public void setPasswordHash(String hash)      { this.password_hash = hash; }


    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String email;
        private String password_hash;
        private String first_name;
        private String last_name;
        private Role   role = Role.CUSTOMER;

        public Builder email(String email)                { this.email = email; return this; }
        public Builder passwordHash(String password_hash) { this.password_hash = password_hash; return this; }
        public Builder firstName(String first_name)       { this.first_name = first_name; return this; }
        public Builder lastName(String last_name)         { this.last_name = last_name; return this; }
        public Builder role(Role role)                    { this.role = role; return this; }
        public User build()                               { return new User(this); }
    }
}