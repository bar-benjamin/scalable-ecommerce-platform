package com.ecommerce.cart.service;

import com.ecommerce.cart.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class CartExpiryService {

    private static final Logger log = LoggerFactory.getLogger(CartExpiryService.class);

    private final CartRepository cart_repository;
    private final long expiry_minutes;

    public CartExpiryService(CartRepository cart_repository,
                             @Value("${cart.expiry-minutes}") long expiry_minutes) {
        this.cart_repository = cart_repository;
        this.expiry_minutes  = expiry_minutes;
    }

    @Scheduled(cron = "${cart.cleanup-cron}")
    @Transactional
    public void purgeExpiredCarts() {
        Instant expiry_threshold = Instant.now().minus(expiry_minutes, ChronoUnit.MINUTES);

        int items_deleted = cart_repository.deleteExpiredCartItems(expiry_threshold);
        int carts_deleted = cart_repository.deleteExpiredCarts(expiry_threshold);

        if (carts_deleted > 0) {
            log.info("Cart expiry job: purged {} cart(s) and {} item(s) " +
                            "(inactive for more than {} minutes)",
                    carts_deleted, items_deleted, expiry_minutes);
        } else {
            log.debug("Cart expiry job: no expired carts found");
        }
    }
}