package com.ecommerce.order.service;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.CartItemResponse;
import com.ecommerce.order.dto.CartResponse;
import com.ecommerce.order.dto.StockDeductRequest;
import com.ecommerce.order.domain.Order;
import com.ecommerce.order.domain.OrderItem;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.PlaceOrderRequest;
import com.ecommerce.order.exception.EmptyCartException;
import com.ecommerce.order.exception.OrderNotFoundException;
import com.ecommerce.order.messaging.OrderEventProducer;
import com.ecommerce.order.messaging.event.OrderItemEvent;
import com.ecommerce.order.messaging.event.OrderPlacedEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final com.ecommerce.order.repository.OrderRepository order_repository;
    private final CartClient cart_client;
    private final ProductClient product_client;
    private final OrderEventProducer event_producer;

    public OrderService(com.ecommerce.order.repository.OrderRepository order_repository,
                        CartClient cart_client,
                        ProductClient product_client,
                        OrderEventProducer event_producer) {
        this.order_repository = order_repository;
        this.cart_client      = cart_client;
        this.product_client   = product_client;
        this.event_producer   = event_producer;
    }

    @Transactional
    public OrderResponse placeOrder(Long user_id, String user_email, PlaceOrderRequest request) {
        // 1. Fetch the user's current cart
        CartResponse cart = cart_client.getCart(user_id.toString());

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new EmptyCartException();
        }

        // 2. Build the order entity from cart contents
        Order order = Order.builder()
                .userId(user_id)
                .totalAmount(cart.getTotal())
                .shippingAddress(request.getShippingAddress())
                .build();

        Order saved_order = order_repository.save(order);

        // 3. Create order items from cart items
        List<OrderItem> order_items = cart.getItems().stream()
                .map(cart_item -> OrderItem.builder()
                        .order(saved_order)
                        .productId(cart_item.getProductId())
                        .productName(cart_item.getProductName())
                        .unitPrice(cart_item.getUnitPrice())
                        .quantity(cart_item.getQuantity())
                        .build())
                .toList();

        saved_order.getItems().addAll(order_items);
        order_repository.save(saved_order);

        // 4. Deduct stock for each item in product-service.
        for (CartItemResponse item : cart.getItems()) {
            product_client.deductStock(item.getProductId(),
                    new StockDeductRequest(item.getQuantity()));
        }

        // 5. Clear the user's cart
        cart_client.clearCart(user_id.toString());

        List<OrderItemEvent> item_events = order_items.stream()
                .map(oi -> OrderItemEvent.builder()
                        .productId(oi.getProductId())
                        .productName(oi.getProductName())
                        .unitPrice(oi.getUnitPrice())
                        .quantity(oi.getQuantity())
                        .build())
                .toList();

        OrderPlacedEvent event = OrderPlacedEvent.builder()
                .orderId(saved_order.getId())
                .userId(user_id)
                .userEmail(user_email)
                .totalAmount(saved_order.getTotalAmount())
                .shippingAddress(saved_order.getShippingAddress())
                .items(item_events)
                .build();

        // 6. Publish event so payment-service and notification-service
        event_producer.publishOrderPlaced(event);

        return OrderResponse.from(saved_order);
    }

    @Transactional(readOnly = true)
    public Page<OrderResponse> getOrderHistory(Long user_id, Pageable pageable) {
        return order_repository.findAllByUserId(user_id, pageable)
                .map(OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long order_id, Long user_id, String role) {
        // ADMINs can view any order. Users only see their own.
        Order order = role.equals("ADMIN")
                ? order_repository.findByIdWithItems(order_id)
                .orElseThrow(() -> new OrderNotFoundException(order_id))
                : order_repository.findByIdAndUserIdWithItems(order_id, user_id)
                .orElseThrow(() -> new OrderNotFoundException(order_id));

        return OrderResponse.from(order);
    }

    @Transactional
    public void markOrderAsPaid(Long order_id) {
        Order order = order_repository.findById(order_id)
                .orElseThrow(() -> new OrderNotFoundException(order_id));

        order.setStatus(Order.OrderStatus.PAID);
        order_repository.save(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Long order_id, Long user_id) {
        Order order = order_repository.findByIdAndUserIdWithItems(order_id, user_id)
                .orElseThrow(() -> new OrderNotFoundException(order_id));

        if (order.getStatus() != Order.OrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING orders can be cancelled. Current status: " + order.getStatus());
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        return OrderResponse.from(order_repository.save(order));
    }
}