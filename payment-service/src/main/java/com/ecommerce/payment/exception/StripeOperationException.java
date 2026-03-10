package com.ecommerce.payment.exception;

public class StripeOperationException extends RuntimeException {

    public StripeOperationException(String message) {
        super(message);
    }
}