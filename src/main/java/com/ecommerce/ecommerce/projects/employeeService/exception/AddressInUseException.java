package com.ecommerce.ecommerce.projects.employeeService.exception;

public class AddressInUseException extends RuntimeException {
    public AddressInUseException() {
        super("This address is assigned to an order and cannot be deleted.");
    }
}