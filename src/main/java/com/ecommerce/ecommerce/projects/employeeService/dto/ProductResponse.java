package com.ecommerce.ecommerce.projects.employeeService.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Instant createdAt,
        Long categoryId,
        String categoryName) {
}