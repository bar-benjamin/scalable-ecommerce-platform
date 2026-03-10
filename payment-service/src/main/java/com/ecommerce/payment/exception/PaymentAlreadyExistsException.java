package com.ecommerce.payment.exception;

public class PaymentAlreadyExistsException extends RuntimeException {

    public PaymentAlreadyExistsException(Long order_id) {
        super("Payment already exists for order_id: " + order_id);
    }
}