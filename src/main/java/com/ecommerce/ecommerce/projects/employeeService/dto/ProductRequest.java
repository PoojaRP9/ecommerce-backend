package com.ecommerce.ecommerce.projects.employeeService.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record ProductRequest(
        @NotBlank @Size(max = 100) String sku,
        @NotBlank @Size(max = 200) String name,
        String description,
        @NotNull @DecimalMin("0.00") BigDecimal price,
        @NotNull @PositiveOrZero Integer stockQuantity,
        @NotNull Long categoryId) {
}