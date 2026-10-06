package com.ecommerce.ecommerce.projects.employeeService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.ecommerce.projects.employeeService.dto.OrderItemRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.OrderPlacementRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.OrderResponse;
import com.ecommerce.ecommerce.projects.employeeService.entity.Address;
import com.ecommerce.ecommerce.projects.employeeService.entity.Order;
import com.ecommerce.ecommerce.projects.employeeService.entity.Product;
import com.ecommerce.ecommerce.projects.employeeService.entity.User;
import com.ecommerce.ecommerce.projects.employeeService.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.projects.employeeService.repository.AddressRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.OrderRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.ProductRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void pricesItemsFromProductsAndCopiesOwnedAddress() {
        User customer = new User();
        customer.setId(7L);
        Address address = address();
        Product product = product(12L, "19.95", 5);
        OrderPlacementRequest request = new OrderPlacementRequest(
                3L, List.of(new OrderItemRequest(12L, 2)));
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(customer));
        when(addressRepository.findByIdAndUser_Id(3L, 7L)).thenReturn(Optional.of(address));
        when(productRepository.findAllById(any())).thenReturn(List.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderResponse response = orderService.placeOrder("jane@example.com", request);

        assertEquals(new BigDecimal("39.90"), response.totalAmount());
        assertEquals("Original street", response.shippingStreet());
        assertEquals(12L, response.items().getFirst().productId());
        assertEquals(new BigDecimal("19.95"), response.items().getFirst().unitPrice());
        assertEquals(2, response.items().getFirst().quantity());
        verify(productRepository).findAllById(any());
        verify(orderRepository).save(Mockito.argThat(order ->
                order.getTotalAmount().equals(new BigDecimal("39.90"))
                        && order.getItems().getFirst().getUnitPrice().equals(new BigDecimal("19.95"))
                        && order.getShippingCity().equals("Boston")));
    }

    @Test
    void rejectsUnknownProductsBeforeSavingOrder() {
        User customer = new User();
        customer.setId(7L);
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(customer));
        when(addressRepository.findByIdAndUser_Id(3L, 7L)).thenReturn(Optional.of(address()));
        when(productRepository.findAllById(any())).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder(
                "jane@example.com", new OrderPlacementRequest(3L, List.of(new OrderItemRequest(404L, 1)))));

        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void rejectsProductsThatCannotFulfillRequestedQuantity() {
        User customer = new User();
        customer.setId(7L);
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(customer));
        when(addressRepository.findByIdAndUser_Id(3L, 7L)).thenReturn(Optional.of(address()));
        when(productRepository.findAllById(any())).thenReturn(List.of(product(12L, "19.95", 0)));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderService.placeOrder("jane@example.com",
                        new OrderPlacementRequest(3L, List.of(new OrderItemRequest(12L, 1)))));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(orderRepository, never()).save(any(Order.class));
    }

    private Address address() {
        Address address = new Address();
        address.setStreet("Original street");
        address.setCity("Boston");
        address.setState("MA");
        address.setPostalCode("02108");
        address.setCountry("US");
        return address;
    }

    private Product product(Long id, String price, int stock) {
        Product product = new Product();
        product.setId(id);
        product.setName("Catalog product");
        product.setPrice(new BigDecimal(price));
        product.setStockQuantity(stock);
        return product;
    }
}