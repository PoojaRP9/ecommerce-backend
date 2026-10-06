package com.ecommerce.ecommerce.projects.employeeService.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.ecommerce.projects.employeeService.dto.CategoryRequest;
import com.ecommerce.ecommerce.projects.employeeService.entity.Category;
import com.ecommerce.ecommerce.projects.employeeService.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.projects.employeeService.repository.CategoryRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategory(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    public Category createCategory(CategoryRequest request) {
        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Long id, CategoryRequest request) {
        Category category = getCategory(id);
        category.setName(request.name());
        category.setDescription(request.description());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = getCategory(id);
        if (productRepository.existsByCategoryId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Category has products and cannot be deleted");
        }
        categoryRepository.delete(category);
    }
}