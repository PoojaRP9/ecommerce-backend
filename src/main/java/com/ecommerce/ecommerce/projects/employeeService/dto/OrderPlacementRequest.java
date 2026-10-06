package com.ecommerce.ecommerce.projects.employeeService.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderPlacementRequest(
        @NotNull @Positive Long addressId,
        @NotEmpty List<@Valid OrderItemRequest> items
) {
}