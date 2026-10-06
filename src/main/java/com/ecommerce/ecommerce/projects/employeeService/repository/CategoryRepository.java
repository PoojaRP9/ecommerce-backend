package com.ecommerce.ecommerce.projects.employeeService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.ecommerce.projects.employeeService.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}