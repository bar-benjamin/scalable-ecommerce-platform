package com.ecommerce.order.repository;

import com.ecommerce.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUserId(Long user_id, Pageable pageable);

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT o FROM Order o JOIN FETCH o.items WHERE o.id = :id AND o.user_id = :user_id")
    Optional<Order> findByIdAndUserIdWithItems(@Param("id") Long id,
                                               @Param("user_id") Long user_id);
}