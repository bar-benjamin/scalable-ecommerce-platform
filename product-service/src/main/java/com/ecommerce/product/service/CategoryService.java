package com.ecommerce.product.service;

import com.ecommerce.product.domain.Category;
import com.ecommerce.product.dto.CategoryRequest;
import com.ecommerce.product.dto.CategoryResponse;
import com.ecommerce.product.exception.CategoryAlreadyExistsException;
import com.ecommerce.product.exception.CategoryNotFoundException;
import com.ecommerce.product.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository category_repository;

    public CategoryService(CategoryRepository category_repository) {
        this.category_repository = category_repository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return category_repository.findAll()
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        return category_repository.findById(id)
                .map(CategoryResponse::from)
                .orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        if (category_repository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException(request.getName());
        }

        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        return CategoryResponse.from(category_repository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = category_repository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(id));

        if (!category.getName().equals(request.getName()) &&
                category_repository.existsByName(request.getName())) {
            throw new CategoryAlreadyExistsException(request.getName());
        }

        category.setName(request.getName());
        category.setDescription(request.getDescription());

        return CategoryResponse.from(category_repository.save(category));
    }
}