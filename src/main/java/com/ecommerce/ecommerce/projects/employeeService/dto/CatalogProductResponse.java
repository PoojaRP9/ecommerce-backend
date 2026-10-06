package com.ecommerce.ecommerce.projects.employeeService.dto;

import java.math.BigDecimal;

public record CatalogProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        boolean available,
        String categoryName) {
}