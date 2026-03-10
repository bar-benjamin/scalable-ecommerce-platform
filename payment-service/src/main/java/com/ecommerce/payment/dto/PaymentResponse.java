package com.ecommerce.payment.dto;

import com.ecommerce.payment.domain.Payment;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentResponse {

    private final Long id;
    private final Long order_id;
    private final Long user_id;
    private final String stripe_payment_intent_id;
    private final BigDecimal amount;
    private final String currency;
    private final String status;
    private final String failure_reason;
    private final Instant created_at;
    private final Instant updated_at;

    private PaymentResponse(Long id, Long order_id, Long user_id,
                            String stripe_payment_intent_id,
                            BigDecimal amount, String currency,
                            String status, String failure_reason,
                            Instant created_at, Instant updated_at) {
        this.id                       = id;
        this.order_id                 = order_id;
        this.user_id                  = user_id;
        this.stripe_payment_intent_id = stripe_payment_intent_id;
        this.amount                   = amount;
        this.currency                 = currency;
        this.status                   = status;
        this.failure_reason           = failure_reason;
        this.created_at               = created_at;
        this.updated_at               = updated_at;
    }

    public static PaymentResponse from(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getOrderId(),
                payment.getUserId(),
                payment.getStripePaymentIntentId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getStatus().name(),
                payment.getFailureReason(),
                payment.getCreatedAt(),
                payment.getUpdatedAt());
    }

    public Long getId()                      { return id; }
    public Long getOrderId()                 { return order_id; }
    public Long getUserId()                  { return user_id; }
    public String getStripePaymentIntentId() { return stripe_payment_intent_id; }
    public BigDecimal getAmount()            { return amount; }
    public String getCurrency()              { return currency; }
    public String getStatus()               { return status; }
    public String getFailureReason()         { return failure_reason; }
    public Instant getCreatedAt()            { return created_at; }
    public Instant getUpdatedAt()            { return updated_at; }
}