package com.ecommerce.order.messaging;

import com.ecommerce.order.messaging.event.OrderPlacedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class OrderEventProducer {
    private static final Logger log = LoggerFactory.getLogger(OrderEventProducer.class);

    private final KafkaTemplate<String, Object> kafka_template;
    private final String order_placed_topic;

    public OrderEventProducer(KafkaTemplate<String, Object> kafka_template,
                              @Value("${kafka.topics.order-placed}") String order_placed_topic) {
        this.kafka_template     = kafka_template;
        this.order_placed_topic = order_placed_topic;
    }

    public void publishOrderPlaced(OrderPlacedEvent event) {
        // Use order_id as the Kafka message key
        String key = event.getOrderId().toString();

        CompletableFuture<SendResult<String, Object>> future = kafka_template.send(order_placed_topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish OrderPlacedEvent for order_id={}: {}",
                        event.getOrderId(), ex.getMessage(), ex);
            } else {
                log.info("Published OrderPlacedEvent for order_id={}",
                        event.getOrderId());
            }
        });
    }
}