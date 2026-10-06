package com.ecommerce.ecommerce.projects.employeeService.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.ecommerce.ecommerce.projects.employeeService.dto.RegisterRequest;
import com.ecommerce.ecommerce.projects.employeeService.entity.User;
import com.ecommerce.ecommerce.projects.employeeService.exception.EmailAlreadyRegisteredException;
import com.ecommerce.ecommerce.projects.employeeService.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private org.springframework.security.authentication.AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void rejectsAnEmailThatIsAlreadyRegistered() {
        RegisterRequest request = new RegisterRequest("Jane Customer", "jane@example.com", "StrongPass123!");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(EmailAlreadyRegisteredException.class, () -> authService.register(request));

        verify(userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void translatesUniqueConstraintViolationToFriendlyRegistrationError() {
        RegisterRequest request = new RegisterRequest("Jane Customer", "jane@example.com", "StrongPass123!");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded-password");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("duplicate key"));

        assertThrows(EmailAlreadyRegisteredException.class, () -> authService.register(request));
    }

    @Test
    void encodesThePasswordProvidedDuringRegistration() {
        RegisterRequest request = new RegisterRequest("Jane Customer", "jane@example.com", "StrongPass123!");
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
        when(userRepository.saveAndFlush(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).saveAndFlush(userCaptor.capture());
        assertEquals("encoded-password", userCaptor.getValue().getPassword());
        verify(passwordEncoder).encode("StrongPass123!");
    }
}