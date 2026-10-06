package com.ecommerce.ecommerce.projects.employeeService.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.ecommerce.projects.employeeService.dto.ApiError;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void returnsConsistentErrorEnvelopeForInvalidRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/products");
        ResponseStatusException exception = new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "minPrice must not exceed maxPrice");

        ResponseEntity<ApiError> response = handler.handleResponseStatus(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody().timestamp());
        assertEquals(400, response.getBody().status());
        assertEquals("Bad Request", response.getBody().error());
        assertEquals("minPrice must not exceed maxPrice", response.getBody().message());
        assertEquals("/api/products", response.getBody().path());
    }

    @Test
    void mapsMissingResourcesAndBusinessFailuresToTheSameEnvelope() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/products/999");

        ResponseEntity<ApiError> missing = handler.handleResourceNotFound(
                new ResourceNotFoundException("Product not found with id: 999"), request);
        ResponseEntity<ApiError> conflict = handler.handleEmailAlreadyRegistered(
                new EmailAlreadyRegisteredException(), request);

        assertEquals(HttpStatus.NOT_FOUND, missing.getStatusCode());
        assertEquals(404, missing.getBody().status());
        assertEquals("/api/products/999", missing.getBody().path());
        assertEquals(HttpStatus.CONFLICT, conflict.getStatusCode());
        assertEquals(409, conflict.getBody().status());
        assertNotNull(conflict.getBody().timestamp());
    }
}