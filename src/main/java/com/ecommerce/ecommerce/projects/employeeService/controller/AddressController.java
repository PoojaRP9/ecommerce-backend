package com.ecommerce.ecommerce.projects.employeeService.controller;

import java.util.List;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.projects.employeeService.dto.AddressPatchRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.AddressRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.AddressResponse;
import com.ecommerce.ecommerce.projects.employeeService.service.AddressService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customer/addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponse> create(
            Authentication authentication,
            @Valid @RequestBody AddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(addressService.create(authentication.getName(), request));
    }

    @GetMapping
    public List<AddressResponse> list(Authentication authentication) {
        return addressService.list(authentication.getName());
    }

    @PutMapping("/{addressId}")
    public AddressResponse replace(
            Authentication authentication,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequest request) {
        return addressService.replace(authentication.getName(), addressId, request);
    }

    @PatchMapping("/{addressId}")
    public AddressResponse patch(
            Authentication authentication,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressPatchRequest request) {
        return addressService.patch(authentication.getName(), addressId, request);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(Authentication authentication, @PathVariable Long addressId) {
        addressService.delete(authentication.getName(), addressId);
        return ResponseEntity.noContent().build();
    }
}