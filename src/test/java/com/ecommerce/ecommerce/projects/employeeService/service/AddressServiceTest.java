package com.ecommerce.ecommerce.projects.employeeService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecommerce.ecommerce.projects.employeeService.dto.AddressResponse;
import com.ecommerce.ecommerce.projects.employeeService.dto.AddressRequest;
import com.ecommerce.ecommerce.projects.employeeService.entity.Address;
import com.ecommerce.ecommerce.projects.employeeService.entity.User;
import com.ecommerce.ecommerce.projects.employeeService.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.projects.employeeService.repository.AddressRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AddressService addressService;

    @Test
    void listsOnlyAddressesOwnedByAuthenticatedCustomer() {
        User customer = customer(7L);
        Address address = address(12L, customer);
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(customer));
        when(addressRepository.findAllByUser_IdOrderByIdAsc(7L)).thenReturn(List.of(address));

        List<AddressResponse> result = addressService.list("jane@example.com");

        assertEquals(1, result.size());
        assertEquals(12L, result.getFirst().id());
        verify(addressRepository).findAllByUser_IdOrderByIdAsc(7L);
    }

    @Test
    void cannotAccessAddressOwnedByAnotherCustomer() {
        User customer = customer(7L);
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(customer));
        when(addressRepository.findByIdAndUser_Id(99L, 7L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> addressService.replace("jane@example.com", 99L,
                        new AddressRequest("1 Main St", "Boston", "MA", "02108", "US")));

        verify(addressRepository).findByIdAndUser_Id(99L, 7L);
    }

    private User customer(Long id) {
        User customer = new User();
        customer.setId(id);
        customer.setEmail("jane@example.com");
        return customer;
    }

    private Address address(Long id, User customer) {
        Address address = new Address();
        address.setId(id);
        address.setUser(customer);
        address.setStreet("1 Main St");
        address.setCity("Boston");
        address.setState("MA");
        address.setPostalCode("02108");
        address.setCountry("US");
        return address;
    }
}