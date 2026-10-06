package com.ecommerce.ecommerce.projects.employeeService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.projects.employeeService.dto.OrderPlacementRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.OrderResponse;
import com.ecommerce.ecommerce.projects.employeeService.service.OrderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            Authentication authentication,
            @Valid @RequestBody OrderPlacementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.placeOrder(authentication.getName(), request));
    }
}