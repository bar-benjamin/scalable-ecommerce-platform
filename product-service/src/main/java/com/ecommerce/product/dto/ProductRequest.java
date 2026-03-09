package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than zero")
    @Digits(integer = 10, fraction = 2, message = "Price must have at most 2 decimal places")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stock_quantity;

    @NotNull(message = "Category id is required")
    private Long category_id;

    public ProductRequest() {}

    public String getName()          { return name; }
    public String getDescription()   { return description; }
    public BigDecimal getPrice()     { return price; }
    public Integer getStockQuantity(){ return stock_quantity; }
    public Long getCategoryId()      { return category_id; }

    public void setName(String name)                     { this.name = name; }
    public void setDescription(String description)       { this.description = description; }
    public void setPrice(BigDecimal price)               { this.price = price; }
    public void setStockQuantity(Integer stock_quantity) { this.stock_quantity = stock_quantity; }
    public void setCategoryId(Long category_id)          { this.category_id = category_id; }
}