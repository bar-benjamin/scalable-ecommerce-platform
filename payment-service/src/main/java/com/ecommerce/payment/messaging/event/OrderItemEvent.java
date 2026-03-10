package com.ecommerce.payment.messaging.event;

import java.math.BigDecimal;

public class OrderItemEvent {

    private Long product_id;
    private String product_name;
    private BigDecimal unit_price;
    private Integer quantity;

    public OrderItemEvent() {}

    public Long getProductId()       { return product_id; }
    public String getProductName()   { return product_name; }
    public BigDecimal getUnitPrice() { return unit_price; }
    public Integer getQuantity()     { return quantity; }

    public void setProductId(Long product_id)        { this.product_id = product_id; }
    public void setProductName(String product_name)  { this.product_name = product_name; }
    public void setUnitPrice(BigDecimal unit_price)  { this.unit_price = unit_price; }
    public void setQuantity(Integer quantity)        { this.quantity = quantity; }
}