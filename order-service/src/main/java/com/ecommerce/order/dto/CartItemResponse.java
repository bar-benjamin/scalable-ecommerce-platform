package com.ecommerce.order.dto;

import java.math.BigDecimal;

public class CartItemResponse {

    private Long id;
    private Long product_id;
    private String product_name;
    private BigDecimal unit_price;
    private Integer quantity;
    private BigDecimal subtotal;

    public CartItemResponse() {}

    public Long getId()              { return id; }
    public Long getProductId()       { return product_id; }
    public String getProductName()   { return product_name; }
    public BigDecimal getUnitPrice() { return unit_price; }
    public Integer getQuantity()     { return quantity; }
    public BigDecimal getSubtotal()  { return subtotal; }

    public void setId(Long id)                       { this.id = id; }
    public void setProductId(Long product_id)        { this.product_id = product_id; }
    public void setProductName(String product_name)  { this.product_name = product_name; }
    public void setUnitPrice(BigDecimal unit_price)  { this.unit_price = unit_price; }
    public void setQuantity(Integer quantity)        { this.quantity = quantity; }
    public void setSubtotal(BigDecimal subtotal)     { this.subtotal = subtotal; }
}