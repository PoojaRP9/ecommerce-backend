package com.ecommerce.ecommerce.projects.employeeService.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.ecommerce.projects.employeeService.dto.ProductRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.ProductResponse;
import com.ecommerce.ecommerce.projects.employeeService.dto.CatalogProductResponse;
import com.ecommerce.ecommerce.projects.employeeService.entity.Category;
import com.ecommerce.ecommerce.projects.employeeService.entity.Product;
import com.ecommerce.ecommerce.projects.employeeService.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.projects.employeeService.repository.CategoryRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<ProductResponse> getProducts(
            Long categoryId,
            int page,
            int size,
            String sortBy,
            String direction) {
        Pageable pageable = createPageable(page, size, sortBy, direction);
        validateCategory(categoryId);
        Page<Product> products = categoryId == null
                ? productRepository.findAll(pageable)
                : productRepository.findByCategoryId(categoryId, pageable);
        return products.map(this::toResponse);
    }

    public Page<CatalogProductResponse> getCatalogProducts(
            Long categoryId,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String name,
            boolean availableOnly,
            int page,
            int size,
            String sortBy,
            String direction) {
        if ((minPrice != null && minPrice.signum() < 0) || (maxPrice != null && maxPrice.signum() < 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "price filters must be nonnegative");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "minPrice must not exceed maxPrice");
        }

        Pageable pageable = createPageable(page, size, sortBy, direction);
        validateCategory(categoryId);
        String nameKeyword = name == null || name.isBlank() ? null : name.trim();
        return productRepository.searchCatalog(
                categoryId, minPrice, maxPrice, nameKeyword, availableOnly, pageable)
                .map(this::toCatalogResponse);
    }

    public ProductResponse getProduct(Long id) {
        return toResponse(findProduct(id));
    }

    public CatalogProductResponse getCatalogProduct(Long id) {
        return toCatalogResponse(findProduct(id));
    }

    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProduct(id);
        applyRequest(product, request);
        return toResponse(productRepository.save(product));
    }

    public void deleteProduct(Long id) {
        productRepository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private Pageable createPageable(int page, int size, String sortBy, String direction) {
        if (page < 0 || size < 1 || size > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "page must be nonnegative and size must be 1-100");
        }

        String sortProperty = switch (sortBy) {
            case "price" -> "price";
            case "name" -> "name";
            case "createdAt" -> "createdAt";
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "sortBy must be one of: price, name, createdAt");
        };

        Sort.Direction sortDirection;
        try {
            sortDirection = Sort.Direction.fromString(direction);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "direction must be asc or desc");
        }

        return PageRequest.of(
                page,
                size,
                Sort.by(sortDirection, sortProperty).and(Sort.by(Sort.Direction.ASC, "id")));
    }

    private void validateCategory(Long categoryId) {
        if (categoryId != null && !categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found with id: " + categoryId);
        }
    }

    private void applyRequest(Product product, ProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Category not found with id: " + request.categoryId()));
        product.setSku(request.sku());
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setStockQuantity(request.stockQuantity());
        product.setCategory(category);
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCreatedAt(),
                product.getCategory().getId(),
                product.getCategory().getName());
    }

    private CatalogProductResponse toCatalogResponse(Product product) {
        return new CatalogProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity() > 0,
                product.getCategory().getName());
    }
}