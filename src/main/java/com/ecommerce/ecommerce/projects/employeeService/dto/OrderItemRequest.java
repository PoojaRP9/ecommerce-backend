package com.ecommerce.ecommerce.projects.employeeService.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderItemRequest(
        @NotNull @Positive Long productId,
        @NotNull @Positive Integer quantity
) {
}