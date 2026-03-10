package com.ecommerce.order.client;

import com.ecommerce.order.dto.CartResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "cart-service", url = "${feign.cart-service.url:}")
public interface CartClient {

    @GetMapping("/api/cart")
    CartResponse getCart(@RequestHeader("X-User-Id") String user_id);

    // Called after order is successfully placed to empty the cart.
    @DeleteMapping("/api/cart")
    void clearCart(@RequestHeader("X-User-Id") String user_id);
}