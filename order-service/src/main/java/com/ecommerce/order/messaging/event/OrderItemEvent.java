package com.ecommerce.order.messaging.event;

import java.math.BigDecimal;

// Events are plain Java objects serialized to JSON by Kafka's JsonSerializer.
public class OrderItemEvent {
    private Long product_id;
    private String product_name;
    private BigDecimal unit_price;
    private Integer quantity;

    public OrderItemEvent() {}

    private OrderItemEvent(Builder builder) {
        this.product_id   = builder.product_id;
        this.product_name = builder.product_name;
        this.unit_price   = builder.unit_price;
        this.quantity     = builder.quantity;
    }

    public Long getProductId()       { return product_id; }
    public String getProductName()   { return product_name; }
    public BigDecimal getUnitPrice() { return unit_price; }
    public Integer getQuantity()     { return quantity; }

    public void setProductId(Long product_id)        { this.product_id = product_id; }
    public void setProductName(String product_name)  { this.product_name = product_name; }
    public void setUnitPrice(BigDecimal unit_price)  { this.unit_price = unit_price; }
    public void setQuantity(Integer quantity)        { this.quantity = quantity; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Long product_id;
        private String product_name;
        private BigDecimal unit_price;
        private Integer quantity;

        public Builder productId(Long product_id)       { this.product_id = product_id; return this; }
        public Builder productName(String product_name) { this.product_name = product_name; return this; }
        public Builder unitPrice(BigDecimal unit_price) { this.unit_price = unit_price; return this; }
        public Builder quantity(Integer quantity)       { this.quantity = quantity; return this; }
        public OrderItemEvent build()                   { return new OrderItemEvent(this); }
    }
}