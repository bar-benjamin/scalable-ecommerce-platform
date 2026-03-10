package com.ecommerce.payment.messaging;

import com.ecommerce.payment.messaging.event.PaymentConfirmedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class PaymentEventProducer {

    private static final Logger log = LoggerFactory.getLogger(PaymentEventProducer.class);

    private final KafkaTemplate<String, Object> kafka_template;
    private final String payment_confirmed_topic;

    public PaymentEventProducer(
            KafkaTemplate<String, Object> kafka_template,
            @Value("${kafka.topics.payment-confirmed}") String payment_confirmed_topic) {
        this.kafka_template          = kafka_template;
        this.payment_confirmed_topic = payment_confirmed_topic;
    }

    public void publishPaymentConfirmed(PaymentConfirmedEvent event) {
        String key = event.getOrderId().toString();

        CompletableFuture<SendResult<String, Object>> future =
                kafka_template.send(payment_confirmed_topic, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish PaymentConfirmedEvent for order_id={}: {}",
                        event.getOrderId(), ex.getMessage(), ex);
            } else {
                log.info("Published PaymentConfirmedEvent for order_id={} partition={} offset={}",
                        event.getOrderId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}