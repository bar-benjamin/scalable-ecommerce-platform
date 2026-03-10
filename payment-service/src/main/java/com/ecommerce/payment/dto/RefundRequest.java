package com.ecommerce.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RefundRequest {

    @NotNull(message = "Order id is required")
    private Long order_id;

    // Stripe accepts: duplicate, fraudulent, requested_by_customer
    private String reason;

    public RefundRequest() {}

    public Long getOrderId() { return order_id; }
    public String getReason(){ return reason; }

    public void setOrderId(Long order_id) { this.order_id = order_id; }
    public void setReason(String reason)  { this.reason = reason; }
}