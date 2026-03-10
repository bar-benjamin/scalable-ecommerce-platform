package com.ecommerce.payment.service;

import com.ecommerce.payment.domain.Payment;
import com.ecommerce.payment.dto.PaymentResponse;
import com.ecommerce.payment.dto.RefundRequest;
import com.ecommerce.payment.exception.PaymentAlreadyExistsException;
import com.ecommerce.payment.exception.PaymentNotFoundException;
import com.ecommerce.payment.exception.PaymentNotRefundableException;
import com.ecommerce.payment.exception.StripeOperationException;
import com.ecommerce.payment.messaging.PaymentEventProducer;
import com.ecommerce.payment.messaging.event.OrderPlacedEvent;
import com.ecommerce.payment.messaging.event.PaymentConfirmedEvent;
import com.ecommerce.payment.repository.PaymentRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository payment_repository;
    private final PaymentEventProducer event_producer;
    private final String currency;

    public PaymentService(PaymentRepository payment_repository,
                          PaymentEventProducer event_producer,
                          @Value("${stripe.currency}") String currency) {
        this.payment_repository = payment_repository;
        this.event_producer     = event_producer;
        this.currency           = currency;
    }

    // Called by OrderEventConsumer when an order-placed Kafka event arrives.
    // Creates a Stripe PaymentIntent and persists a PENDING payment record.
    // The actual charge is confirmed via the Stripe webhook.
    @Transactional
    public void initiatePayment(OrderPlacedEvent event) {
        if (payment_repository.existsByOrderId(event.getOrderId())) {
            log.warn("Payment already exists for order_id={}, skipping duplicate event",
                    event.getOrderId());
            return;
        }

        // Stripe amounts are in the smallest currency unit (cents for USD).
        // Multiply by 100 and convert to long to avoid floating point issues.
        long amount_in_cents = event.getTotalAmount()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        Payment payment = Payment.builder()
                .orderId(event.getOrderId())
                .userId(event.getUserId())
                .amount(event.getTotalAmount())
                .currency(currency)
                .build();

        Payment saved = payment_repository.save(payment);

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount_in_cents)
                    .setCurrency(currency)
                    // automatic_payment_methods lets Stripe decide the best
                    // payment method for the customer's region and device.
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .build())
                    // Metadata links the Stripe object back to our internal IDs.
                    // Visible in the Stripe dashboard and webhook payloads.
                    .putMetadata("order_id", event.getOrderId().toString())
                    .putMetadata("user_id", event.getUserId().toString())
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            saved.setStripePaymentIntentId(intent.getId());
            payment_repository.save(saved);

            log.info("Created PaymentIntent={} for order_id={}",
                    intent.getId(), event.getOrderId());

        } catch (StripeException ex) {
            saved.setStatus(Payment.PaymentStatus.FAILED);
            saved.setFailureReason(ex.getMessage());
            payment_repository.save(saved);

            log.error("Stripe error creating PaymentIntent for order_id={}: {}",
                    event.getOrderId(), ex.getMessage(), ex);
        }
    }

    // Called by the webhook handler when Stripe confirms a payment_intent.succeeded event.
    @Transactional
    public void confirmPayment(String payment_intent_id, String charge_id) {
        Payment payment = payment_repository
                .findByStripePaymentIntentId(payment_intent_id)
                .orElseThrow(() -> new PaymentNotFoundException(payment_intent_id));

        payment.setStatus(Payment.PaymentStatus.SUCCEEDED);
        payment.setStripeChargeId(charge_id);
        payment_repository.save(payment);

        PaymentConfirmedEvent event = PaymentConfirmedEvent.builder()
                .orderId(payment.getOrderId())
                .userId(payment.getUserId())
                .paymentIntentId(payment_intent_id)
                .amountPaid(payment.getAmount())
                .build();

        event_producer.publishPaymentConfirmed(event);

        log.info("Payment confirmed for order_id={} payment_intent={}",
                payment.getOrderId(), payment_intent_id);
    }

    // Called by the webhook handler when Stripe sends a payment_intent.payment_failed event.
    @Transactional
    public void handlePaymentFailed(String payment_intent_id, String failure_reason) {
        Payment payment = payment_repository
                .findByStripePaymentIntentId(payment_intent_id)
                .orElseThrow(() -> new PaymentNotFoundException(payment_intent_id));

        payment.setStatus(Payment.PaymentStatus.FAILED);
        payment.setFailureReason(failure_reason);
        payment_repository.save(payment);

        log.warn("Payment failed for order_id={} reason={}",
                payment.getOrderId(), failure_reason);
    }

    @Transactional
    public PaymentResponse refund(RefundRequest request) {
        Payment payment = payment_repository.findByOrderId(request.getOrderId())
                .orElseThrow(() -> new PaymentNotFoundException(
                        "order_id: " + request.getOrderId()));

        if (payment.getStatus() != Payment.PaymentStatus.SUCCEEDED) {
            throw new PaymentNotRefundableException(payment.getOrderId(),
                    payment.getStatus().name());
        }

        try {
            RefundCreateParams.Builder params_builder = RefundCreateParams.builder()
                    .setCharge(payment.getStripeChargeId());

            if (request.getReason() != null && !request.getReason().isBlank()) {
                params_builder.setReason(
                        RefundCreateParams.Reason.valueOf(
                                request.getReason().toUpperCase()));
            }

            Refund refund = Refund.create(params_builder.build());

            payment.setStatus(Payment.PaymentStatus.REFUNDED);
            payment_repository.save(payment);

            log.info("Refund issued for order_id={} refund_id={}",
                    payment.getOrderId(), refund.getId());

        } catch (StripeException ex) {
            log.error("Stripe refund failed for order_id={}: {}",
                    payment.getOrderId(), ex.getMessage(), ex);
            throw new StripeOperationException("Refund failed: " + ex.getMessage());
        }

        return PaymentResponse.from(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByOrderId(Long order_id) {
        return payment_repository.findByOrderId(order_id)
                .map(PaymentResponse::from)
                .orElseThrow(() -> new PaymentNotFoundException("order_id: " + order_id));
    }
}