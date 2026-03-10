package com.ecommerce.payment.exception;

public class PaymentNotRefundableException extends RuntimeException {

    public PaymentNotRefundableException(Long order_id, String current_status) {
        super(String.format(
                "Payment for order_id %d cannot be refunded. Current status: %s",
                order_id, current_status));
    }
}