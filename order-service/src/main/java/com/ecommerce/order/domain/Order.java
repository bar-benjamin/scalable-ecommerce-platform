package com.ecommerce.order.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders", schema = "order_schema")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long user_id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total_amount;

    @Column(nullable = false)
    private String shipping_address;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL,
            orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private Instant created_at;

    @Column(nullable = false)
    private Instant updated_at;

    public enum OrderStatus {
        PENDING,
        PAID,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED
    }

    protected Order() {}

    private Order(Builder builder) {
        this.user_id          = builder.user_id;
        this.status           = OrderStatus.PENDING;
        this.total_amount     = builder.total_amount;
        this.shipping_address = builder.shipping_address;
        this.created_at       = Instant.now();
        this.updated_at       = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updated_at = Instant.now();
    }

    public Long getId()                { return id; }
    public Long getUserId()            { return user_id; }
    public OrderStatus getStatus()     { return status; }
    public BigDecimal getTotalAmount() { return total_amount; }
    public String getShippingAddress() { return shipping_address; }
    public List<OrderItem> getItems()  { return items; }
    public Instant getCreatedAt()      { return created_at; }
    public Instant getUpdatedAt()      { return updated_at; }

    public void setStatus(OrderStatus status) { this.status = status; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long user_id;
        private BigDecimal total_amount;
        private String shipping_address;

        public Builder userId(Long user_id)                  { this.user_id = user_id; return this; }
        public Builder totalAmount(BigDecimal total_amount)  { this.total_amount = total_amount; return this; }
        public Builder shippingAddress(String address)       { this.shipping_address = address; return this; }
        public Order build()                                 { return new Order(this); }
    }
}
