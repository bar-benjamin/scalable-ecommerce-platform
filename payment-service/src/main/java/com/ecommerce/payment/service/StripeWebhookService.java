package com.ecommerce.payment.service;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Charge;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StripeWebhookService {

    private static final Logger log = LoggerFactory.getLogger(StripeWebhookService.class);

    private final PaymentService payment_service;
    private final String webhook_secret;

    public StripeWebhookService(PaymentService payment_service,
                                @Value("${stripe.webhook-secret}") String webhook_secret) {
        this.payment_service  = payment_service;
        this.webhook_secret   = webhook_secret;
    }

    public void processWebhook(String payload, String sig_header) {
        Event event;

        try {
            // constructEvent verifies that signature using our webhook secret,
            // ensuring the payload actually came from Stripe and not a malicious user.
            event = Webhook.constructEvent(payload, sig_header, webhook_secret);
        } catch (SignatureVerificationException ex) {
            log.warn("Invalid Stripe webhook signature: {}", ex.getMessage());
            throw new SecurityException("Invalid Stripe webhook signature");
        }

        log.info("Received Stripe webhook event type={} id={}", event.getType(), event.getId());

        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        Optional<StripeObject> stripe_object = deserializer.getObject();

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                stripe_object.ifPresent(obj -> {
                    PaymentIntent intent = (PaymentIntent) obj;
                    String charge_id = intent.getLatestCharge();
                    payment_service.confirmPayment(intent.getId(), charge_id);
                });
            }
            case "payment_intent.payment_failed" -> {
                stripe_object.ifPresent(obj -> {
                    PaymentIntent intent = (PaymentIntent) obj;
                    String failure_reason = intent.getLastPaymentError() != null
                            ? intent.getLastPaymentError().getMessage()
                            : "Unknown failure";
                    payment_service.handlePaymentFailed(intent.getId(), failure_reason);
                });
            }
            default -> log.debug("Unhandled Stripe event type: {}", event.getType());
        }
    }
}