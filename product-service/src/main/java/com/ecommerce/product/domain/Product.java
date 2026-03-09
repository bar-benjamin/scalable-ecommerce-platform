package com.ecommerce.product.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "products", schema = "product_schema")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    // Inventory count. Negative stock is prevented at the service layer.
    @Column(nullable = false)
    private Integer stock_quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private Instant created_at;

    @Column(nullable = false)
    private Instant updated_at;

    protected Product() {}

    private Product(Builder builder) {
        this.name           = builder.name;
        this.description    = builder.description;
        this.price          = builder.price;
        this.stock_quantity = builder.stock_quantity;
        this.category       = builder.category;
        this.active         = true;
        this.created_at     = Instant.now();
        this.updated_at     = Instant.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updated_at = Instant.now();
    }

    public Long getId()              { return id; }
    public String getName()          { return name; }
    public String getDescription()   { return description; }
    public BigDecimal getPrice()     { return price; }
    public Integer getStockQuantity(){ return stock_quantity; }
    public Category getCategory()    { return category; }
    public boolean isActive()        { return active; }
    public Instant getCreatedAt()    { return created_at; }
    public Instant getUpdatedAt()    { return updated_at; }

    public void setName(String name)                      { this.name = name; }
    public void setDescription(String description)        { this.description = description; }
    public void setPrice(BigDecimal price)                { this.price = price; }
    public void setStockQuantity(Integer stock_quantity)  { this.stock_quantity = stock_quantity; }
    public void setCategory(Category category)            { this.category = category; }
    public void setActive(boolean active)                 { this.active = active; }

    public void decreaseStock(int quantity) {
        if (this.stock_quantity < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock for product id: " + this.id);
        }

        this.stock_quantity -= quantity;
    }

    public void increaseStock(int quantity) {
        this.stock_quantity += quantity;
    }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String name;
        private String description;
        private BigDecimal price;
        private Integer stock_quantity;
        private Category category;

        public Builder name(String name)                     { this.name = name; return this; }
        public Builder description(String description)       { this.description = description; return this; }
        public Builder price(BigDecimal price)               { this.price = price; return this; }
        public Builder stockQuantity(Integer stock_quantity) { this.stock_quantity = stock_quantity; return this; }
        public Builder category(Category category)           { this.category = category; return this; }
        public Product build()                               { return new Product(this); }
    }
}