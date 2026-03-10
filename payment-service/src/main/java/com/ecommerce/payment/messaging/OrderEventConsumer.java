package com.ecommerce.payment.messaging;

import com.ecommerce.payment.messaging.event.OrderPlacedEvent;
import com.ecommerce.payment.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final PaymentService payment_service;

    public OrderEventConsumer(PaymentService payment_service) {
        this.payment_service = payment_service;
    }

    @KafkaListener(
            topics           = "${kafka.topics.order-placed}",
            groupId          = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order_id={} user_id={} amount={}",
                event.getOrderId(), event.getUserId(), event.getTotalAmount());

        try {
            payment_service.initiatePayment(event);
        } catch (Exception ex) {
            log.error("Failed to initiate payment for order_id={}: {}",
                    event.getOrderId(), ex.getMessage(), ex);
        }
    }
}