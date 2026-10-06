package com.ecommerce.ecommerce.projects.employeeService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.ecommerce.projects.employeeService.entity.User;
import com.ecommerce.ecommerce.projects.employeeService.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadsCustomerAccountByEmailForAuthentication() {
        User customer = new User();
        customer.setEmail("jane@example.com");
        customer.setUsername("jane@example.com");
        customer.setPassword("encoded-password");
        customer.setRole("USER");
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(customer));

        UserDetails result = userDetailsService.loadUserByUsername("jane@example.com");

        assertEquals("jane@example.com", result.getUsername());
        assertEquals("encoded-password", result.getPassword());
        assertEquals("ROLE_USER", result.getAuthorities().iterator().next().getAuthority());
        verify(userRepository).findByEmail("jane@example.com");
    }
}