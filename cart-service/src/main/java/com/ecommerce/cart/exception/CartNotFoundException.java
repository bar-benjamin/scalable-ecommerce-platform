package com.ecommerce.cart.exception;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException(Long user_id) {
        super("Cart not found for user id: " + user_id);
    }
}