package com.ecommerce.order.client;

import com.ecommerce.order.dto.StockDeductRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "product-service", url = "${feign.product-service.url:}")
public interface ProductClient {
    @PostMapping("/api/products/{id}/stock/deduct")
    void deductStock(@PathVariable("id") Long product_id,
                     @RequestBody StockDeductRequest request);
}