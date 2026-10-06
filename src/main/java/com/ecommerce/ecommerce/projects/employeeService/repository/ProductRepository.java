package com.ecommerce.ecommerce.projects.employeeService.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ecommerce.ecommerce.projects.employeeService.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsByCategoryId(Long categoryId);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("""
            select product from Product product
            where (:categoryId is null or product.category.id = :categoryId)
              and (:minPrice is null or product.price >= :minPrice)
              and (:maxPrice is null or product.price <= :maxPrice)
              and (:name is null or lower(product.name) like lower(concat('%', :name, '%')))
              and (:availableOnly = false or product.stockQuantity > 0)
            """)
    Page<Product> searchCatalog(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("name") String name,
            @Param("availableOnly") boolean availableOnly,
            Pageable pageable);
}