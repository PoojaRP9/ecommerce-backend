package com.ecommerce.ecommerce.projects.employeeService.dto;

import jakarta.validation.constraints.Size;

public record AddressPatchRequest(
        @Size(max = 255) String street,
        @Size(max = 100) String city,
        @Size(max = 100) String state,
        @Size(max = 20) String postalCode,
        @Size(max = 100) String country
) {
    public boolean isEmpty() {
        return street == null && city == null && state == null
                && postalCode == null && country == null;
    }
}