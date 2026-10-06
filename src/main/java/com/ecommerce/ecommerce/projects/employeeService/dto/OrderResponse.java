package com.ecommerce.ecommerce.projects.employeeService.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import com.ecommerce.ecommerce.projects.employeeService.entity.OrderStatus;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal totalAmount,
        Instant orderedAt,
        String shippingStreet,
        String shippingCity,
        String shippingState,
        String shippingPostalCode,
        String shippingCountry,
        List<OrderItemResponse> items
) {
}