package com.ecommerce.payment.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;

public class PaymentConfirmedEvent {

    private Long order_id;
    private Long user_id;
    private String payment_intent_id;
    private BigDecimal amount_paid;
    private Instant confirmed_at;

    public PaymentConfirmedEvent() {}

    private PaymentConfirmedEvent(Builder builder) {
        this.order_id          = builder.order_id;
        this.user_id           = builder.user_id;
        this.payment_intent_id = builder.payment_intent_id;
        this.amount_paid       = builder.amount_paid;
        this.confirmed_at      = Instant.now();
    }

    public Long getOrderId()           { return order_id; }
    public Long getUserId()            { return user_id; }
    public String getPaymentIntentId() { return payment_intent_id; }
    public BigDecimal getAmountPaid()  { return amount_paid; }
    public Instant getConfirmedAt()    { return confirmed_at; }

    public void setOrderId(Long order_id)                    { this.order_id = order_id; }
    public void setUserId(Long user_id)                      { this.user_id = user_id; }
    public void setPaymentIntentId(String payment_intent_id) { this.payment_intent_id = payment_intent_id; }
    public void setAmountPaid(BigDecimal amount_paid)        { this.amount_paid = amount_paid; }
    public void setConfirmedAt(Instant confirmed_at)         { this.confirmed_at = confirmed_at; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long order_id;
        private Long user_id;
        private String payment_intent_id;
        private BigDecimal amount_paid;

        public Builder orderId(Long order_id)                  { this.order_id = order_id; return this; }
        public Builder userId(Long user_id)                    { this.user_id = user_id; return this; }
        public Builder paymentIntentId(String id)              { this.payment_intent_id = id; return this; }
        public Builder amountPaid(BigDecimal amount_paid)      { this.amount_paid = amount_paid; return this; }
        public PaymentConfirmedEvent build()                   { return new PaymentConfirmedEvent(this); }
    }
}