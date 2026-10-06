package com.ecommerce.ecommerce.projects.employeeService.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.ecommerce.projects.employeeService.dto.LoginRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.LoginResponse;
import com.ecommerce.ecommerce.projects.employeeService.dto.RegisterRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.RegisterResponse;
import com.ecommerce.ecommerce.projects.employeeService.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}