package com.ecommerce.order.dto;

import com.ecommerce.order.domain.Order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderResponse {

    private final Long id;
    private final Long user_id;
    private final String status;
    private final BigDecimal total_amount;
    private final String shipping_address;
    private final List<OrderItemResponse> items;
    private final Instant created_at;
    private final Instant updated_at;

    private OrderResponse(Long id, Long user_id, String status,
                          BigDecimal total_amount, String shipping_address,
                          List<OrderItemResponse> items,
                          Instant created_at, Instant updated_at) {
        this.id               = id;
        this.user_id          = user_id;
        this.status           = status;
        this.total_amount     = total_amount;
        this.shipping_address = shipping_address;
        this.items            = items;
        this.created_at       = created_at;
        this.updated_at       = updated_at;
    }

    public static OrderResponse from(Order order) {
        List<OrderItemResponse> item_responses = order.getItems()
                .stream()
                .map(OrderItemResponse::from)
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getShippingAddress(),
                item_responses,
                order.getCreatedAt(),
                order.getUpdatedAt());
    }

    public Long getId()                      { return id; }
    public Long getUserId()                  { return user_id; }
    public String getStatus()               { return status; }
    public BigDecimal getTotalAmount()       { return total_amount; }
    public String getShippingAddress()       { return shipping_address; }
    public List<OrderItemResponse> getItems(){ return items; }
    public Instant getCreatedAt()            { return created_at; }
    public Instant getUpdatedAt()            { return updated_at; }
}