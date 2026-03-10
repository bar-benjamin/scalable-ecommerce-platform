package com.ecommerce.order.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items", schema = "order_schema")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(nullable = false)
    private Long product_id;

    @Column(nullable = false)
    private String product_name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unit_price;

    @Column(nullable = false)
    private Integer quantity;

    protected OrderItem() {}

    private OrderItem(Builder builder) {
        this.order        = builder.order;
        this.product_id   = builder.product_id;
        this.product_name = builder.product_name;
        this.unit_price   = builder.unit_price;
        this.quantity     = builder.quantity;
    }

    public Long getId()              { return id; }
    public Order getOrder()          { return order; }
    public Long getProductId()       { return product_id; }
    public String getProductName()   { return product_name; }
    public BigDecimal getUnitPrice() { return unit_price; }
    public Integer getQuantity()     { return quantity; }

    public BigDecimal getSubtotal() {
        return unit_price.multiply(BigDecimal.valueOf(quantity));
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Order order;
        private Long product_id;
        private String product_name;
        private BigDecimal unit_price;
        private Integer quantity;

        public Builder order(Order order)                { this.order = order; return this; }
        public Builder productId(Long product_id)       { this.product_id = product_id; return this; }
        public Builder productName(String name)         { this.product_name = name; return this; }
        public Builder unitPrice(BigDecimal unit_price) { this.unit_price = unit_price; return this; }
        public Builder quantity(Integer quantity)       { this.quantity = quantity; return this; }
        public OrderItem build()                        { return new OrderItem(this); }
    }
}