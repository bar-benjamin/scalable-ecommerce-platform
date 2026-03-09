package com.ecommerce.product.repository;

import com.ecommerce.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findAllByActiveTrue(Pageable pageable);

    Page<Product> findAllByCategoryIdAndActiveTrue(Long category_id, Pageable pageable);

    Optional<Product> findByIdAndActiveTrue(Long id);

    @Query("SELECT p FROM Product p JOIN FETCH p.category WHERE p.id = :id AND p.active = true")
    Optional<Product> findActiveByIdWithCategory(@Param("id") Long id);

    @Query("""
            SELECT p FROM Product p JOIN FETCH p.category
            WHERE (:category_id IS NULL OR p.category.id = :category_id) AND p.active = true
            """)
    Page<Product> searchProducts(@Param("category_id") Long category_id, Pageable pageable);
}
