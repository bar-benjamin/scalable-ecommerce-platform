package com.ecommerce.cart.client;

import com.ecommerce.cart.client.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service", url = "${feign.product-service.url:}")
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    ProductResponse getProductById(@PathVariable("id") Long id);
}