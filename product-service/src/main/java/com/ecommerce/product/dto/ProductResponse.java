package com.ecommerce.product.dto;

import com.ecommerce.product.domain.Product;

import java.math.BigDecimal;
import java.time.Instant;

public class ProductResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final Integer stock_quantity;
    private final CategoryResponse category;
    private final boolean active;
    private final Instant created_at;
    private final Instant updated_at;

    private ProductResponse(Long id, String name, String description,
                            BigDecimal price, Integer stock_quantity,
                            CategoryResponse category, boolean active,
                            Instant created_at, Instant updated_at) {
        this.id             = id;
        this.name           = name;
        this.description    = description;
        this.price          = price;
        this.stock_quantity = stock_quantity;
        this.category       = category;
        this.active         = active;
        this.created_at     = created_at;
        this.updated_at     = updated_at;
    }

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                CategoryResponse.from(product.getCategory()),
                product.isActive(),
                product.getCreatedAt(),
                product.getUpdatedAt());
    }

    public Long getId()              { return id; }
    public String getName()          { return name; }
    public String getDescription()   { return description; }
    public BigDecimal getPrice()     { return price; }
    public Integer getStockQuantity(){ return stock_quantity; }
    public CategoryResponse getCategory() { return category; }
    public boolean isActive()        { return active; }
    public Instant getCreatedAt()    { return created_at; }
    public Instant getUpdatedAt()    { return updated_at; }
}