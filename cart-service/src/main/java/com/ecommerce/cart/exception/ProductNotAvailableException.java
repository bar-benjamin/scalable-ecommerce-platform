package com.ecommerce.cart.exception;

public class ProductNotAvailableException extends RuntimeException {
    public ProductNotAvailableException(Long product_id) {
        super("Product is not available: " + product_id);
    }
}