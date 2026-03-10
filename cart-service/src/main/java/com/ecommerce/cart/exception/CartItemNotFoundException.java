package com.ecommerce.cart.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(Long product_id) {
        super("Cart item not found for product id: " + product_id);
    }
}