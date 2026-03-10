package com.ecommerce.cart.client.dto;

import com.ecommerce.cart.domain.CartItem;

import java.math.BigDecimal;

public class CartItemResponse {

    private final Long id;
    private final Long product_id;
    private final String product_name;
    private final BigDecimal unit_price;
    private final Integer quantity;
    private final BigDecimal subtotal;

    private CartItemResponse(Long id, Long product_id, String product_name,
                             BigDecimal unit_price, Integer quantity,
                             BigDecimal subtotal) {
        this.id           = id;
        this.product_id   = product_id;
        this.product_name = product_name;
        this.unit_price   = unit_price;
        this.quantity     = quantity;
        this.subtotal     = subtotal;
    }

    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(
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