package com.ecommerce.cart.client.dto;

import java.math.BigDecimal;

public class ProductResponse {

    private Long id;
    private String name;
    private BigDecimal price;
    private Integer stock_quantity;
    private boolean active;

    public ProductResponse() {}

    public Long getId()              { return id; }
    public String getName()          { return name; }
    public BigDecimal getPrice()     { return price; }
    public Integer getStockQuantity(){ return stock_quantity; }
    public boolean isActive()        { return active; }

    public void setId(Long id)                       { this.id = id; }
    public void setName(String name)                 { this.name = name; }
    public void setPrice(BigDecimal price)           { this.price = price; }
    public void setStockQuantity(Integer qty)        { this.stock_quantity = qty; }
    public void setActive(boolean active)            { this.active = active; }
}