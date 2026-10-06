package com.ecommerce.ecommerce.projects.employeeService.service;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.ecommerce.ecommerce.projects.employeeService.dto.AddressPatchRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.AddressRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.AddressResponse;
import com.ecommerce.ecommerce.projects.employeeService.entity.Address;
import com.ecommerce.ecommerce.projects.employeeService.entity.User;
import com.ecommerce.ecommerce.projects.employeeService.exception.AddressInUseException;
import com.ecommerce.ecommerce.projects.employeeService.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.projects.employeeService.repository.AddressRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressResponse create(String customerEmail, AddressRequest request) {
        User customer = getCustomer(customerEmail);
        Address address = new Address();
        address.setUser(customer);
        apply(address, request);
        return toResponse(addressRepository.save(address));
    }

    public List<AddressResponse> list(String customerEmail) {
        User customer = getCustomer(customerEmail);
        return addressRepository.findAllByUser_IdOrderByIdAsc(customer.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    public AddressResponse replace(String customerEmail, Long addressId, AddressRequest request) {
        Address address = getOwnedAddress(customerEmail, addressId);
        apply(address, request);
        return toResponse(addressRepository.save(address));
    }

    public AddressResponse patch(String customerEmail, Long addressId, AddressPatchRequest request) {
        if (request.isEmpty()) {
            throw new IllegalArgumentException("At least one address field must be provided.");
        }

        Address address = getOwnedAddress(customerEmail, addressId);
        if (request.street() != null) {
            address.setStreet(requireNonBlank(request.street(), "street"));
        }
        if (request.city() != null) {
            address.setCity(requireNonBlank(request.city(), "city"));
        }
        if (request.state() != null) {
            address.setState(request.state());
        }
        if (request.postalCode() != null) {
            address.setPostalCode(requireNonBlank(request.postalCode(), "postalCode"));
        }
        if (request.country() != null) {
            address.setCountry(requireNonBlank(request.country(), "country"));
        }
        return toResponse(addressRepository.save(address));
    }

    public void delete(String customerEmail, Long addressId) {
        Address address = getOwnedAddress(customerEmail, addressId);
        try {
            addressRepository.delete(address);
            addressRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new AddressInUseException();
        }
    }

    private User getCustomer(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found"));
    }

    private Address getOwnedAddress(String customerEmail, Long addressId) {
        User customer = getCustomer(customerEmail);
        return addressRepository.findByIdAndUser_Id(addressId, customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
    }

    private void apply(Address address, AddressRequest request) {
        address.setStreet(request.street());
        address.setCity(request.city());
        address.setState(request.state());
        address.setPostalCode(request.postalCode());
        address.setCountry(request.country());
    }

    private String requireNonBlank(String value, String fieldName) {
        if (value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value;
    }

    private AddressResponse toResponse(Address address) {
        return new AddressResponse(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getPostalCode(),
                address.getCountry());
    }
}