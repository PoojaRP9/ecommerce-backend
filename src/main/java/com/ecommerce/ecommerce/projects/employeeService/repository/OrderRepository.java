package com.ecommerce.ecommerce.projects.employeeService.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.projects.employeeService.entity.Order;
import com.ecommerce.ecommerce.projects.employeeService.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findAllByUser_IdOrderByOrderedAtDesc(Long userId, Pageable pageable);

    Optional<Order> findByIdAndUser_Id(Long orderId, Long userId);

    Page<Order> findAllByStatus(OrderStatus status, Pageable pageable);
}