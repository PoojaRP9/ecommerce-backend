package com.ecommerce.ecommerce.projects.employeeService.dto;

public record AddressResponse(
        Long id,
        String street,
        String city,
        String state,
        String postalCode,
        String country
) {
}