package com.ecommerce.order.dto;

import java.math.BigDecimal;
import java.util.List;

public class CartResponse {

    private Long id;
    private Long user_id;
    private List<CartItemResponse> items;
    private BigDecimal total;

    public CartResponse() {}

    public Long getId()                      { return id; }
    public Long getUserId()                  { return user_id; }
    public List<CartItemResponse> getItems() { return items; }
    public BigDecimal getTotal()             { return total; }

    public void setId(Long id)                           { this.id = id; }
    public void setUserId(Long user_id)                  { this.user_id = user_id; }
    public void setItems(List<CartItemResponse> items)   { this.items = items; }
    public void setTotal(BigDecimal total)               { this.total = total; }
}