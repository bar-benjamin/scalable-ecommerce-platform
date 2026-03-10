package com.ecommerce.order.dto;

import com.ecommerce.order.domain.OrderItem;

import java.math.BigDecimal;

public class OrderItemResponse {

    private final Long id;
    private final Long product_id;
    private final String product_name;
    private final BigDecimal unit_price;
    private final Integer quantity;
    private final BigDecimal subtotal;

    private OrderItemResponse(Long id, Long product_id, String product_name,
                              BigDecimal unit_price, Integer quantity,
                              BigDecimal subtotal) {
        this.id           = id;
        this.product_id   = product_id;
        this.product_name = product_name;
        this.unit_price   = unit_price;
        this.quantity     = quantity;
        this.subtotal     = subtotal;
    }

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getUnitPrice(),
                item.getQuantity(),
                item.getSubtotal());
    }

    public Long getId()              { return id; }
    public Long getProductId()       { return product_id; }
    public String getProductName()   { return product_name; }
    public BigDecimal getUnitPrice() { return unit_price; }
    public Integer getQuantity()     { return quantity; }
    public BigDecimal getSubtotal()  { return subtotal; }
}