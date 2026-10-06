package com.ecommerce.ecommerce.projects.employeeService.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.projects.employeeService.dto.LoginRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.LoginResponse;
import com.ecommerce.ecommerce.projects.employeeService.dto.RegisterRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.RegisterResponse;
import com.ecommerce.ecommerce.projects.employeeService.entity.User;
import com.ecommerce.ecommerce.projects.employeeService.exception.EmailAlreadyRegisteredException;
import com.ecommerce.ecommerce.projects.employeeService.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor 
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException();
        }

        User user = new User();
        user.setUsername(request.email());
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole("USER");

        User savedUser;
        try {
            savedUser = userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyRegisteredException();
        }
        return new RegisterResponse(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
    }

    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String token = jwtService.generateToken(
            request.username(),
            authentication.getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList());

        return new LoginResponse(token);
    }
}
