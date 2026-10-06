package com.ecommerce.ecommerce.projects.employeeService.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {
    public EmailAlreadyRegisteredException() {
        super("An account with this email already exists. Please log in or use a different email.");
    }
}