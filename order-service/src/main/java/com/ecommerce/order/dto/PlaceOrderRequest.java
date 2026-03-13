package com.ecommerce.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class PlaceOrderRequest {

    @NotBlank(message = "Shipping address is required")
    private String shipping_address;

    public PlaceOrderRequest() {}

    public String getShippingAddress() { return shipping_address; }
    public void setShippingAddress(String shipping_address) {
        this.shipping_address = shipping_address;
    }
}