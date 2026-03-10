package com.ecommerce.cart.client.dto;

import com.ecommerce.cart.domain.Cart;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class CartResponse {

    private final Long id;
    private final Long user_id;
    private final List<CartItemResponse> items;
    private final BigDecimal total;
    private final Instant created_at;
    private final Instant updated_at;

    private CartResponse(Long id, Long user_id, List<CartItemResponse> items,
                         BigDecimal total, Instant created_at, Instant updated_at) {
        this.id         = id;
        this.user_id    = user_id;
        this.items      = items;
        this.total      = total;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public static CartResponse from(Cart cart) {
        List<CartItemResponse> item_responses = cart.getItems()
                .stream()
                .map(CartItemResponse::from)
                .toList();

        BigDecimal total = item_responses.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                item_responses,
                total,
                cart.getCreatedAt(),
                cart.getUpdatedAt());
    }

    public Long getId()                      { return id; }
    public Long getUserId()                  { return user_id; }
    public List<CartItemResponse> getItems() { return items; }
    public BigDecimal getTotal()             { return total; }
    public Instant getCreatedAt()            { return created_at; }
    public Instant getUpdatedAt()            { return updated_at; }
}