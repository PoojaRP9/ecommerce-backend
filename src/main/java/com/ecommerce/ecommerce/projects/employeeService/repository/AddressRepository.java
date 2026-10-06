package com.ecommerce.ecommerce.projects.employeeService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.projects.employeeService.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findAllByUser_IdOrderByIdAsc(Long userId);

    Optional<Address> findByIdAndUser_Id(Long id, Long userId);
}