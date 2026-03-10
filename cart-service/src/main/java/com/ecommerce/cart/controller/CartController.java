package com.ecommerce.cart.controller;

import com.ecommerce.cart.client.dto.*;
import com.ecommerce.cart.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cart_service;

    public CartController(CartService cart_service) {
        this.cart_service = cart_service;
    }

    @GetMapping
    public ResponseEntity<CartResponse> getCart(
            @RequestHeader("X-User-Id") String user_id) {

        return ResponseEntity.ok(cart_service.getCart(Long.parseLong(user_id)));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponse> addItem(
            @RequestHeader("X-User-Id") String user_id,
            @Valid @RequestBody AddItemRequest request) {

        return ResponseEntity.ok(cart_service.addItem(Long.parseLong(user_id), request));
    }

    @PutMapping("/items/{product_id}")
    public ResponseEntity<CartResponse> updateItem(
            @RequestHeader("X-User-Id") String user_id,
            @PathVariable Long product_id,
            @Valid @RequestBody UpdateItemRequest request) {

        return ResponseEntity.ok(
                cart_service.updateItem(Long.parseLong(user_id), product_id, request));
    }

    @DeleteMapping("/items/{product_id}")
    public ResponseEntity<CartResponse> removeItem(
            @RequestHeader("X-User-Id") String user_id,
            @PathVariable Long product_id) {

        return ResponseEntity.ok(
                cart_service.removeItem(Long.parseLong(user_id), product_id));
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart(
            @RequestHeader("X-User-Id") String user_id) {

        cart_service.clearCart(Long.parseLong(user_id));
        return ResponseEntity.noContent().build();
    }
}