package com.ecommerce.cart.repository;

import com.ecommerce.cart.domain.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    @Query("SELECT c FROM Cart c WHERE c.user_id = :user_id")
    Optional<Cart> findByUserId(@Param("user_id") Long user_id);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Cart c WHERE c.user_id = :user_id")
    boolean existsByUserId(@Param("user_id") Long user_id);

    @Modifying
    @Query("DELETE FROM Cart c WHERE c.updated_at < :expiry_threshold")
    int deleteExpiredCarts(@Param("expiry_threshold") Instant expiry_threshold);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart IN " +
            "(SELECT c FROM Cart c WHERE c.updated_at < :expiry_threshold)")
    int deleteExpiredCartItems(@Param("expiry_threshold") Instant expiry_threshold);

    @Query("SELECT c FROM Cart c WHERE c.updated_at < :expiry_threshold")
    List<Cart> findExpiredCarts(@Param("expiry_threshold") Instant expiry_threshold);
}