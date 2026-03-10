package com.ecommerce.cart.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(
        name = "cart_items",
        schema = "cart_schema",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"})
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @Column(nullable = false)
    private Long product_id;

    @Column(nullable = false)
    private String product_name;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal unit_price;

    @Column(nullable = false)
    private Integer quantity;

    protected CartItem() {}

    private CartItem(Builder builder) {
        this.cart         = builder.cart;
        this.product_id   = builder.product_id;
        this.product_name = builder.product_name;
        this.unit_price   = builder.unit_price;
        this.quantity     = builder.quantity;
    }

    public Long getId()              { return id; }
    public Cart getCart()            { return cart; }
    public Long getProductId()       { return product_id; }
    public String getProductName()   { return product_name; }
    public BigDecimal getUnitPrice() { return unit_price; }
    public Integer getQuantity()     { return quantity; }

    public void setQuantity(Integer quantity)     { this.quantity = quantity; }
    public void setUnitPrice(BigDecimal price)    { this.unit_price = price; }
    public void setProductName(String name)       { this.product_name = name; }

    public BigDecimal getSubtotal() {
        return unit_price.multiply(BigDecimal.valueOf(quantity));
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private Cart cart;
        private Long product_id;
        private String product_name;
        private BigDecimal unit_price;
        private Integer quantity;

        public Builder cart(Cart cart)                { this.cart = cart; return this; }
        public Builder productId(Long product_id)     { this.product_id = product_id; return this; }
        public Builder productName(String name)       { this.product_name = name; return this; }
        public Builder unitPrice(BigDecimal price)    { this.unit_price = price; return this; }
        public Builder quantity(Integer quantity)     { this.quantity = quantity; return this; }
        public CartItem build()                       { return new CartItem(this); }
    }
}