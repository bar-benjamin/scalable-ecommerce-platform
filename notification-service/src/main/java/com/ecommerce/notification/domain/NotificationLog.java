package com.ecommerce.notification.domain;

import jakarta.persistence.*;
import java.time.Instant;

// Log every email attempt - both successes and failures.
@Entity
@Table(name = "notification_logs", schema = "notification_schema")
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long order_id;

    @Column(nullable = false)
    private String recipient_email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column
    private String failure_reason;

    @Column(nullable = false, updatable = false)
    private Instant sent_at;

    public enum NotificationType {
        ORDER_CONFIRMATION,
        PAYMENT_CONFIRMATION
    }

    public enum NotificationStatus {
        SENT,
        FAILED
    }

    protected NotificationLog() {}

    private NotificationLog(Builder builder) {
        this.order_id         = builder.order_id;
        this.recipient_email  = builder.recipient_email;
        this.type             = builder.type;
        this.status           = builder.status;
        this.failure_reason   = builder.failure_reason;
        this.sent_at          = Instant.now();
    }

    public Long getId()                   { return id; }
    public Long getOrderId()              { return order_id; }
    public String getRecipientEmail()     { return recipient_email; }
    public NotificationType getType()     { return type; }
    public NotificationStatus getStatus() { return status; }
    public String getFailureReason()      { return failure_reason; }
    public Instant getSentAt()            { return sent_at; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long order_id;
        private String recipient_email;
        private NotificationType type;
        private NotificationStatus status;
        private String failure_reason;

        public Builder orderId(Long order_id)              { this.order_id = order_id; return this; }
        public Builder recipientEmail(String email)        { this.recipient_email = email; return this; }
        public Builder type(NotificationType type)         { this.type = type; return this; }
        public Builder status(NotificationStatus status)   { this.status = status; return this; }
        public Builder failureReason(String reason)        { this.failure_reason = reason; return this; }
        public NotificationLog build()                     { return new NotificationLog(this); }
    }
}