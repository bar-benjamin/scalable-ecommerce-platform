package com.ecommerce.cart.service;

import com.ecommerce.cart.client.ProductClient;
import com.ecommerce.cart.client.dto.ProductResponse;
import com.ecommerce.cart.domain.Cart;
import com.ecommerce.cart.domain.CartItem;
import com.ecommerce.cart.client.dto.*;
import com.ecommerce.cart.exception.CartItemNotFoundException;
import com.ecommerce.cart.exception.CartNotFoundException;
import com.ecommerce.cart.exception.InsufficientStockException;
import com.ecommerce.cart.exception.ProductNotAvailableException;
import com.ecommerce.cart.repository.CartItemRepository;
import com.ecommerce.cart.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {

    private final CartRepository cart_repository;
    private final CartItemRepository cart_item_repository;
    private final ProductClient product_client;

    public CartService(CartRepository cart_repository,
                       CartItemRepository cart_item_repository,
                       ProductClient product_client) {
        this.cart_repository      = cart_repository;
        this.cart_item_repository = cart_item_repository;
        this.product_client       = product_client;
    }

    @Transactional(readOnly = true)
    public CartResponse getCart(Long user_id) {
        Cart cart = cart_repository.findByUserId(user_id)
                .orElseThrow(() -> new CartNotFoundException(user_id));
        return CartResponse.from(cart);
    }

    @Transactional
    public CartResponse addItem(Long user_id, AddItemRequest request) {
        // Check if product exists and is available before touching the cart
        ProductResponse product = product_client.getProductById(request.getProductId());

        if (!product.isActive()) {
            throw new ProductNotAvailableException(request.getProductId());
        }

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(request.getProductId(),
                    request.getQuantity(), product.getStockQuantity());
        }

        // Get or create cart for this user
        Cart cart = cart_repository.findByUserId(user_id)
                .orElseGet(() -> cart_repository.save(
                        Cart.builder().userId(user_id).build()));

        // If the product is already in the cart, increment quantity
        cart_item_repository.findByCartIdAndProductId(cart.getId(), request.getProductId())
                .ifPresentOrElse(
                        existing_item -> {
                            int new_qty = existing_item.getQuantity() + request.getQuantity();
                            if (product.getStockQuantity() < new_qty) {
                                throw new InsufficientStockException(request.getProductId(),
                                        new_qty, product.getStockQuantity());
                            }
                            existing_item.setQuantity(new_qty);
                            cart_item_repository.save(existing_item);
                        },
                        () -> {
                            CartItem new_item = CartItem.builder()
                                    .cart(cart)
                                    .productId(product.getId())
                                    .productName(product.getName())
                                    .unitPrice(product.getPrice())
                                    .quantity(request.getQuantity())
                                    .build();
                            cart_item_repository.save(new_item);
                        }
                );

        cart.touch();
        Cart saved_cart = cart_repository.save(cart);

        // Reload with items to build the response
        return CartResponse.from(cart_repository.findByUserId(user_id).orElse(saved_cart));
    }

    @Transactional
    public CartResponse updateItem(Long user_id, Long product_id, UpdateItemRequest request) {
        Cart cart = cart_repository.findByUserId(user_id)
                .orElseThrow(() -> new CartNotFoundException(user_id));

        CartItem item = cart_item_repository
                .findByCartIdAndProductId(cart.getId(), product_id)
                .orElseThrow(() -> new CartItemNotFoundException(product_id));

        // Re-validate stock at update time — stock may have changed since add
        ProductResponse product = product_client.getProductById(product_id);
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(product_id,
                    request.getQuantity(), product.getStockQuantity());
        }

        item.setQuantity(request.getQuantity());
        // Refresh price snapshot in case it changed since the item was added
        item.setUnitPrice(product.getPrice());
        item.setProductName(product.getName());

        cart_item_repository.save(item);
        cart.touch();
        cart_repository.save(cart);

        return CartResponse.from(cart_repository.findByUserId(user_id).orElse(cart));
    }

    @Transactional
    public CartResponse removeItem(Long user_id, Long product_id) {
        Cart cart = cart_repository.findByUserId(user_id)
                .orElseThrow(() -> new CartNotFoundException(user_id));

        CartItem item = cart_item_repository
                .findByCartIdAndProductId(cart.getId(), product_id)
                .orElseThrow(() -> new CartItemNotFoundException(product_id));

        cart.getItems().remove(item);
        cart_item_repository.delete(item);
        cart.touch();
        cart_repository.save(cart);

        return CartResponse.from(cart_repository.findByUserId(user_id).orElse(cart));
    }

    @Transactional
    public void clearCart(Long user_id) {
        Cart cart = cart_repository.findByUserId(user_id)
                .orElseThrow(() -> new CartNotFoundException(user_id));

        cart.getItems().clear();
        cart.touch();
        cart_repository.save(cart);
    }
}