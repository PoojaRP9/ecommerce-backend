package com.ecommerce.ecommerce.projects.employeeService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.ecommerce.projects.employeeService.entity.Category;
import com.ecommerce.ecommerce.projects.employeeService.entity.Product;
import com.ecommerce.ecommerce.projects.employeeService.dto.CatalogProductResponse;
import com.ecommerce.ecommerce.projects.employeeService.repository.CategoryRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void filtersByCategoryAndAppliesRequestedPageAndSort() {
        when(categoryRepository.existsById(12L)).thenReturn(true);
        when(productRepository.findByCategoryId(eq(12L), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getProducts(12L, 2, 15, "price", "asc");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).findByCategoryId(eq(12L), pageableCaptor.capture());
        verify(productRepository, never()).findAll(any(Pageable.class));

        Pageable pageable = pageableCaptor.getValue();
        assertEquals(2, pageable.getPageNumber());
        assertEquals(15, pageable.getPageSize());
        assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("price").getDirection());
        Assertions.assertEquals(Sort.Direction.ASC, pageable.getSort().getOrderFor("id").getDirection());
    }

    @Test
    void rejectsUnapprovedSortProperties() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.getProducts(null, 0, 20, "category.name", "asc"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void rejectsPageSizesAboveTheMaximum() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> productService.getProducts(null, 0, 101, "name", "asc"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void searchesCatalogByPriceNameAvailabilityAndCategory() {
        when(categoryRepository.existsById(12L)).thenReturn(true);
        when(productRepository.searchCatalog(
                eq(12L), eq(new BigDecimal("10.00")), eq(new BigDecimal("50.00")), eq("phone"), eq(true),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));

        productService.getCatalogProducts(
                12L, new BigDecimal("10.00"), new BigDecimal("50.00"), " phone ", true,
                1, 10, "price", "asc");

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(productRepository).searchCatalog(
                eq(12L), eq(new BigDecimal("10.00")), eq(new BigDecimal("50.00")), eq("phone"), eq(true),
                pageableCaptor.capture());
        assertEquals(1, pageableCaptor.getValue().getPageNumber());
        assertEquals(Sort.Direction.ASC, pageableCaptor.getValue().getSort().getOrderFor("price").getDirection());
    }

    @Test
    void rejectsCatalogPriceRangeWhenMinimumExceedsMaximum() {
        ResponseStatusException exception = assertThrows(
            ResponseStatusException.class,
            () -> productService.getCatalogProducts(
                null, new BigDecimal("50.00"), new BigDecimal("10.00"), null, false,
                0, 20, "createdAt", "desc"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(productRepository, never()).searchCatalog(
            any(), any(), any(), any(), eq(false), any(Pageable.class));
    }

    @Test
    void mapsStockToCustomerFacingAvailability() {
        Category category = new Category();
        category.setName("Phones");
        Product product = new Product();
        product.setId(7L);
        product.setName("Example phone");
        product.setDescription("A sample product");
        product.setPrice(new BigDecimal("25.00"));
        product.setStockQuantity(3);
        product.setCategory(category);
        when(productRepository.findById(7L)).thenReturn(Optional.of(product));

        CatalogProductResponse response = productService.getCatalogProduct(7L);

        assertEquals("Example phone", response.name());
        assertEquals(new BigDecimal("25.00"), response.price());
        assertTrue(response.available());
        assertEquals("Phones", response.categoryName());
    }
}
