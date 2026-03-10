package com.ecommerce.payment.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payments", schema = "payment_schema")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long order_id;

    @Column(nullable = false)
    private Long user_id;

    @Column(unique = true)
    private String stripe_payment_intent_id;

    @Column
    private String stripe_charge_id;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column
    private String failure_reason;

    @Column(nullable = false, updatable = false)
    private Instant created_at;

    @Column(nullable = false)
    private Instant updated_at;

    public enum PaymentStatus {
        PENDING,
        SUCCEEDED,
        FAILED,
        REFUNDED
    }

    protected Payment() {}

    private Payment(Builder builder) {
        this.order_id  = builder.order_id;
        this.user_id   = builder.user_id;
        this.amount    = builder.amount;
        this.currency  = builder.currency;
        this.status    = PaymentStatus.PENDING;
        this.created_at = Instant.now();
        this.updated_at = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updated_at = Instant.now();
    }

    public Long getId()                      { return id; }
    public Long getOrderId()                 { return order_id; }
    public Long getUserId()                  { return user_id; }
    public String getStripePaymentIntentId() { return stripe_payment_intent_id; }
    public String getStripeChargeId()        { return stripe_charge_id; }
    public BigDecimal getAmount()            { return amount; }
    public String getCurrency()              { return currency; }
    public PaymentStatus getStatus()         { return status; }
    public String getFailureReason()         { return failure_reason; }
    public Instant getCreatedAt()            { return created_at; }
    public Instant getUpdatedAt()            { return updated_at; }

    public void setStripePaymentIntentId(String id)  { this.stripe_payment_intent_id = id; }
    public void setStripeChargeId(String id)         { this.stripe_charge_id = id; }
    public void setStatus(PaymentStatus status)      { this.status = status; }
    public void setFailureReason(String reason)      { this.failure_reason = reason; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long order_id;
        private Long user_id;
        private BigDecimal amount;
        private String currency;

        public Builder orderId(Long order_id)      { this.order_id = order_id; return this; }
        public Builder userId(Long user_id)        { this.user_id = user_id; return this; }
        public Builder amount(BigDecimal amount)   { this.amount = amount; return this; }
        public Builder currency(String currency)   { this.currency = currency; return this; }
        public Payment build()                     { return new Payment(this); }
    }
}