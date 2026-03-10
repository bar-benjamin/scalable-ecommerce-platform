package com.ecommerce.cart.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts", schema = "cart_schema")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long user_id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant created_at;

    // updated_at is refreshed on every cart mutation. The expiry scheduler
    // uses this field to determine whether a cart has gone stale.
    @Column(nullable = false)
    private Instant updated_at;

    protected Cart() {}

    private Cart(Builder builder) {
        this.user_id    = builder.user_id;
        this.created_at = Instant.now();
        this.updated_at = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updated_at = Instant.now();
    }

    public void touch() {
        this.updated_at = Instant.now();
    }

    public Long getId()              { return id; }
    public Long getUserId()          { return user_id; }
    public List<CartItem> getItems() { return items; }
    public Instant getCreatedAt()    { return created_at; }
    public Instant getUpdatedAt()    { return updated_at; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long user_id;

        public Builder userId(Long user_id) { this.user_id = user_id; return this; }
        public Cart build()                 { return new Cart(this); }
    }
}