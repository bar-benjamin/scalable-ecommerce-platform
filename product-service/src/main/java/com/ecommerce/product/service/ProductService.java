package com.ecommerce.product.service;

import com.ecommerce.product.domain.Category;
import com.ecommerce.product.domain.Product;
import com.ecommerce.product.dto.*;
import com.ecommerce.product.exception.CategoryNotFoundException;
import com.ecommerce.product.exception.InsufficientStockException;
import com.ecommerce.product.exception.ProductNotFoundException;
import com.ecommerce.product.repository.CategoryRepository;
import com.ecommerce.product.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository product_repository;
    private final CategoryRepository category_repository;

    public ProductService(ProductRepository product_repository,
                          CategoryRepository category_repository) {
        this.product_repository  = product_repository;
        this.category_repository = category_repository;
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProducts(Long category_id, Pageable pageable) {
        return product_repository.searchProducts(category_id, pageable)
                .map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        return product_repository.findActiveByIdWithCategory(id)
                .map(ProductResponse::from)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        Category category = category_repository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .category(category)
                .build();

        return ProductResponse.from(product_repository.save(product));
    }

    @Transactional
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = product_repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Category category = category_repository.findById(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setCategory(category);

        return ProductResponse.from(product_repository.save(product));
    }

    @Transactional
    public void deactivateProduct(Long id) {
        Product product = product_repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setActive(false);
        product_repository.save(product);
    }

    @Transactional
    public ProductResponse updateInventory(Long id, InventoryUpdateRequest request) {
        Product product = product_repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        product.setStockQuantity(request.getQuantity());

        return ProductResponse.from(product_repository.save(product));
    }

    @Transactional
    public void deductStock(Long id, StockDeductRequest request) {
        Product product = product_repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (product.getStockQuantity() < request.getQuantity()) {
            throw new InsufficientStockException(id, request.getQuantity(),
                    product.getStockQuantity());
        }

        product.decreaseStock(request.getQuantity());
        product_repository.save(product);
    }
}