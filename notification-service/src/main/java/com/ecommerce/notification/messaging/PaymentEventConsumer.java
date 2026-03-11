package com.ecommerce.notification.messaging;

import com.ecommerce.notification.messaging.event.PaymentConfirmedEvent;
import com.ecommerce.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventConsumer.class);

    private final NotificationService notification_service;

    public PaymentEventConsumer(NotificationService notification_service) {
        this.notification_service = notification_service;
    }

    @KafkaListener(
            topics           = "${kafka.topics.payment-confirmed}",
            groupId          = "${kafka.consumer.group.payment}",
            containerFactory = "paymentKafkaListenerContainerFactory"
    )
    public void handlePaymentConfirmed(PaymentConfirmedEvent event) {
        log.info("Received PaymentConfirmedEvent for order_id={} amount={}",
                event.getOrderId(), event.getAmountPaid());

        try {
            notification_service.sendPaymentConfirmation(event);
        } catch (Exception ex) {
            log.error("Failed to send payment confirmation for order_id={}: {}",
                    event.getOrderId(), ex.getMessage(), ex);
        }
    }
}