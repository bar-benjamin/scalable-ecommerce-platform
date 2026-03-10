package com.ecommerce.payment.controller;

import com.ecommerce.payment.service.StripeWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class WebhookController {

    private static final Logger log = LoggerFactory.getLogger(WebhookController.class);

    private final StripeWebhookService webhook_service;

    public WebhookController(StripeWebhookService webhook_service) {
        this.webhook_service = webhook_service;
    }

    // Handle Stripe Webhook payment
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sig_header) {

        try {
            webhook_service.processWebhook(payload, sig_header);
            return ResponseEntity.ok("Received");
        } catch (SecurityException ex) {
            log.warn("Rejected webhook: {}", ex.getMessage());
            return ResponseEntity.status(400).body("Invalid signature");
        } catch (Exception ex) {
            log.error("Webhook processing error: {}", ex.getMessage(), ex);
            return ResponseEntity.status(500).body("Processing error");
        }
    }
}