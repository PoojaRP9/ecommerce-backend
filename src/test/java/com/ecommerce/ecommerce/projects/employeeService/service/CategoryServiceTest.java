package com.ecommerce.ecommerce.projects.employeeService.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.ecommerce.ecommerce.projects.employeeService.entity.Category;
import com.ecommerce.ecommerce.projects.employeeService.repository.CategoryRepository;
import com.ecommerce.ecommerce.projects.employeeService.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void filtersAndSortsCategoriesInRepositoryQuery() {
        when(categoryRepository.searchCategories(org.mockito.ArgumentMatchers.eq("phones"),
                org.mockito.ArgumentMatchers.any(Sort.class)))
                .thenReturn(List.of());

        categoryService.getAllCategories(" phones ", "name", "desc");

        ArgumentCaptor<Sort> sortCaptor = ArgumentCaptor.forClass(Sort.class);
        verify(categoryRepository).searchCategories(org.mockito.ArgumentMatchers.eq("phones"), sortCaptor.capture());
        assertEquals(Sort.Direction.DESC, sortCaptor.getValue().getOrderFor("name").getDirection());
        assertEquals(Sort.Direction.ASC, sortCaptor.getValue().getOrderFor("id").getDirection());
    }

    @Test
    void rejectsUnsupportedSortProperties() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> categoryService.getAllCategories(null, "description", "asc"));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }
}