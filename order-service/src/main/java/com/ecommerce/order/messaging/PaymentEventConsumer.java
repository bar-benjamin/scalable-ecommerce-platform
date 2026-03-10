package com.ecommerce.order.messaging;

import com.ecommerce.order.messaging.event.PaymentConfirmedEvent;
import com.ecommerce.order.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final OrderService order_service;

    public PaymentEventConsumer(OrderService order_service) {
        this.order_service = order_service;
    }

    @KafkaListener(
            topics           = "${kafka.topics.payment-confirmed}",
            groupId          = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {
        log.info("Received PaymentConfirmedEvent for order_id={} payment_intent={}",
                event.getOrderId(), event.getPaymentIntentId());

        try {
            order_service.markOrderAsPaid(event.getOrderId());
            log.info("Order id={} marked as PAID", event.getOrderId());
        } catch (Exception ex) {
            // Log and swallow — rethrowing here would cause Kafka to retry infinitely
            log.error("Failed to mark order id={} as PAID: {}",
                    event.getOrderId(), ex.getMessage(), ex);
        }
    }
}