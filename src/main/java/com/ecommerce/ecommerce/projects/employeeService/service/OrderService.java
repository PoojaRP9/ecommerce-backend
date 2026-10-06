package com.ecommerce.ecommerce.projects.employeeService.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.ecommerce.projects.employeeService.dto.OrderItemRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.OrderItemResponse;
import com.ecommerce.ecommerce.projects.employeeService.dto.OrderPlacementRequest;
import com.ecommerce.ecommerce.projects.employeeService.dto.OrderResponse;
import com.ecommerce.ecommerce.projects.employeeService.entity.Address;
import com.ecommerce.ecommerce.projects.employeeService.entity.Order;
import com.ecommerce.ecommerce.projects.employeeService.entity.OrderItem;
import com.ecommerce.ecommerce.projects.employeeService.entity.Product;
import com.ecommerce.ecommerce.projects.employeeService.entity.User;
import com.ecommerce.ecommerce.projects.employeeService.exception.ResourceNotFoundException;
import com.ecommerce.ecommerce.projects.employeeService.repository.AddressRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.OrderRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.ProductRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderResponse placeOrder(String customerEmail, OrderPlacementRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer account not found"));
        Address address = addressRepository.findByIdAndUser_Id(request.addressId(), customer.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Set<Long> requestedProductIds = new HashSet<>();
        for (OrderItemRequest itemRequest : request.items()) {
            if (!requestedProductIds.add(itemRequest.productId())) {
                throw new IllegalArgumentException("A product may only appear once in an order.");
            }
        }

        Map<Long, Product> productsById = new HashMap<>();
        for (Product product : productRepository.findAllById(requestedProductIds)) {
            productsById.put(product.getId(), product);
        }
        if (productsById.size() != requestedProductIds.size()) {
            throw new ResourceNotFoundException("One or more requested products do not exist");
        }

        Order order = new Order();
        order.setUser(customer);
        order.setShippingStreet(address.getStreet());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPostalCode(address.getPostalCode());
        order.setShippingCountry(address.getCountry());

        BigDecimal total = BigDecimal.ZERO;
        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productsById.get(itemRequest.productId());
            if (product.getStockQuantity() == null || product.getStockQuantity() < itemRequest.quantity()) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Product is unavailable in the requested quantity: " + product.getId());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(itemRequest.quantity());
            orderItem.setUnitPrice(product.getPrice());
            order.getItems().add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }
        order.setTotalAmount(total);

        return toResponse(orderRepository.save(order));
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.getProduct().getId(),
                        item.getProduct().getName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getOrderedAt(),
                order.getShippingStreet(),
                order.getShippingCity(),
                order.getShippingState(),
                order.getShippingPostalCode(),
                order.getShippingCountry(),
                items);
    }
}