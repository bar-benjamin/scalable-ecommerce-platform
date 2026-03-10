package com.ecommerce.cart.exception;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long product_id, int requested, int available) {
        super(String.format(
                "Insufficient stock for product id %d: requested %d, available %d",
                product_id, requested, available));
    }
}