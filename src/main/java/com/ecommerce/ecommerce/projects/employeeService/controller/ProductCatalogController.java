package com.ecommerce.ecommerce.projects.employeeService.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.projects.employeeService.dto.CatalogProductResponse;
import com.ecommerce.ecommerce.projects.employeeService.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductCatalogController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<Page<CatalogProductResponse>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "false") boolean availableOnly) {
        return ResponseEntity.ok(productService.getCatalogProducts(
                categoryId, minPrice, maxPrice, name, availableOnly, page, size, sortBy, direction));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogProductResponse> getProduct(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getCatalogProduct(id));
    }
}