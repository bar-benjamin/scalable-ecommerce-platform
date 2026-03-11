package com.ecommerce.notification.messaging;

import com.ecommerce.notification.messaging.event.OrderPlacedEvent;
import com.ecommerce.notification.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final NotificationService notification_service;

    public OrderEventConsumer(NotificationService notification_service) {
        this.notification_service = notification_service;
    }

    @KafkaListener(
            topics           = "${kafka.topics.order-placed}",
            groupId          = "${kafka.consumer.group.order}",
            containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void handleOrderPlaced(OrderPlacedEvent event) {
        log.info("Received OrderPlacedEvent for order_id={} email={}",
                event.getOrderId(), event.getUserEmail());

        try {
            notification_service.sendOrderConfirmation(event);
        } catch (Exception ex) {
            log.error("Failed to send order confirmation for order_id={}: {}",
                    event.getOrderId(), ex.getMessage(), ex);
        }
    }
}