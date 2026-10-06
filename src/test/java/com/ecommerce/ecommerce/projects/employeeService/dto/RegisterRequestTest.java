package com.ecommerce.ecommerce.projects.employeeService.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

class RegisterRequestTest {

    @Test
    void acceptsValidNameAndEmail() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            assertTrue(validator.validate(new RegisterRequest("Jane Customer", "jane@example.com", "StrongPass123!")).isEmpty());
        }
    }

    @Test
    void rejectsInvalidNameAndMissingEmail() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
                Set<String> invalidFields = validator.validate(new RegisterRequest("123 Customer", null, null))
                    .stream()
                    .map(violation -> violation.getPropertyPath().toString())
                    .collect(Collectors.toSet());

                assertEquals(Set.of("name", "email", "password"), invalidFields);
        }
    }

    @Test
    void rejectsMalformedEmail() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            assertTrue(validator.validate(new RegisterRequest("Jane Customer", "not-an-email", "StrongPass123!"))
                    .stream()
                    .anyMatch(violation -> violation.getPropertyPath().toString().equals("email")));
        }
    }

    @Test
    void rejectsShortPassword() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();

            assertTrue(validator.validate(new RegisterRequest("Jane Customer", "jane@example.com", "short"))
                    .stream()
                    .anyMatch(violation -> violation.getPropertyPath().toString().equals("password")));
        }
    }
}