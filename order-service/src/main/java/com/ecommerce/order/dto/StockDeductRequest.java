package com.ecommerce.order.dto;

public class StockDeductRequest {

    private Integer quantity;

    public StockDeductRequest() {}

    public StockDeductRequest(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
}