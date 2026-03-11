package com.ecommerce.notification.messaging.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderPlacedEvent {

    private Long order_id;
    private Long user_id;
    private String user_email;
    private BigDecimal total_amount;
    private String shipping_address;
    private List<OrderItemEvent> items;
    private Instant placed_at;

    public OrderPlacedEvent() {}

    public Long getOrderId()               { return order_id; }
    public Long getUserId()                { return user_id; }
    public String getUserEmail()           { return user_email; }
    public BigDecimal getTotalAmount()     { return total_amount; }
    public String getShippingAddress()     { return shipping_address; }
    public List<OrderItemEvent> getItems() { return items; }
    public Instant getPlacedAt()           { return placed_at; }

    public void setOrderId(Long order_id)                  { this.order_id = order_id; }
    public void setUserId(Long user_id)                    { this.user_id = user_id; }
    public void setUserEmail(String user_email)            { this.user_email = user_email; }
    public void setTotalAmount(BigDecimal total_amount)    { this.total_amount = total_amount; }
    public void setShippingAddress(String address)         { this.shipping_address = address; }
    public void setItems(List<OrderItemEvent> items)       { this.items = items; }
    public void setPlacedAt(Instant placed_at)             { this.placed_at = placed_at; }
}