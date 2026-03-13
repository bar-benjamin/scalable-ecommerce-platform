package com.ecommerce.cart.repository;

import com.ecommerce.cart.domain.CartItem;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cart_id AND ci.product_id = :product_id")
    Optional<CartItem> findByCartIdAndProductId(@Param("cart_id") Long cart_id,
                                                @Param("product_id") Long product_id);
}