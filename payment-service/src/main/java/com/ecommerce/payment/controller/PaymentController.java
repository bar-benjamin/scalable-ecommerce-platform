package com.ecommerce.payment.controller;

import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.dto.RefundRequest;
import com.ecommerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService payment_service;

    public PaymentController(PaymentService payment_service) {
        this.payment_service = payment_service;
    }

    @GetMapping("/order/{order_id}")
    public ResponseEntity<PaymentResponse> getPaymentByOrderId(
            @PathVariable Long order_id,
            @RequestHeader("X-User-Id") String user_id,
            @RequestHeader("X-User-Role") String role) {

        return ResponseEntity.ok(payment_service.getPaymentByOrderId(order_id));
    }

    @PostMapping("/refund")
    public ResponseEntity<PaymentResponse> refund(
            @Valid @RequestBody RefundRequest request,
            @RequestHeader("X-User-Role") String role) {

        if (!role.equals("ADMIN")) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(payment_service.refund(request));
    }
}