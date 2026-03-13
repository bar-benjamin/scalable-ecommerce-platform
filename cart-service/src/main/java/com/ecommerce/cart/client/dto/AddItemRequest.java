package com.ecommerce.cart.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class AddItemRequest {

    @NotNull(message = "Product id is required")
    private Long product_id;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    public AddItemRequest() {}

    public Long getProductId()   { return product_id; }
    public Integer getQuantity() { return quantity; }

    public void setProductId(Long product_id)   { this.product_id = product_id; }
    public void setQuantity(Integer quantity)   { this.quantity = quantity; }
}