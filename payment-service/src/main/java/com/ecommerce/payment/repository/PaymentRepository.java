package com.ecommerce.payment.repository;

import com.ecommerce.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long order_id);

    Optional<Payment> findByStripePaymentIntentId(String payment_intent_id);

    boolean existsByOrderId(Long order_id);
}