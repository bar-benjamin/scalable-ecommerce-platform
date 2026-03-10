package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.PlaceOrderRequest;
import com.ecommerce.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService order_service;

    public OrderController(OrderService order_service) {
        this.order_service = order_service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @Valid @RequestBody PlaceOrderRequest request,
            @RequestHeader("X-User-Id") String user_id,
            @RequestHeader("X-User-Email") String user_email) {

        OrderResponse response = order_service.placeOrder(
                Long.parseLong(user_id), user_email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<OrderResponse>> getOrderHistory(
            @RequestHeader("X-User-Id") String user_id,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {

        return ResponseEntity.ok(
                order_service.getOrderHistory(Long.parseLong(user_id), pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String user_id,
            @RequestHeader("X-User-Role") String user_role) {

        return ResponseEntity.ok(
                order_service.getOrder(id, Long.parseLong(user_id), user_role));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String user_id) {

        return ResponseEntity.ok(
                order_service.cancelOrder(id, Long.parseLong(user_id)));
    }
}