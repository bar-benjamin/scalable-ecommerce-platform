package com.ecommerce.payment.exception;

public class PaymentNotFoundException extends RuntimeException {

    public PaymentNotFoundException(String identifier) {
        super("Payment not found for " + identifier);
    }
}