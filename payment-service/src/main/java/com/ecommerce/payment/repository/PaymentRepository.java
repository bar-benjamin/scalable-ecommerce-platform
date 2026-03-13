package com.ecommerce.payment.repository;

import com.ecommerce.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("SELECT p FROM Payment p WHERE p.order_id = :order_id")
    Optional<Payment> findByOrderId(@Param("order_id") Long order_id);

    @Query("SELECT p FROM Payment p WHERE p.stripe_payment_intent_id = :payment_intent_id")
    Optional<Payment> findByStripePaymentIntentId(@Param("payment_intent_id") String payment_intent_id);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Payment p WHERE p.order_id = :order_id")
    boolean existsByOrderId(@Param("order_id") Long order_id);
}