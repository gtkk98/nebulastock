package com.nebulastock.nebulastock.repository;

import com.nebulastock.nebulastock.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    boolean  existsBySku(String sku);

    // Custom JPQL query — searches by name OR category, case-insensitive
    // Page<T> return type enables automatic pagination
    @Query("SELECT p FROM Product p WHERE " +
            "(:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
            "(:category IS NULL OR LOWER(p.category) = LOWER(:category))")
    Page<Product> findWithFilters(
            @Param("search") String search,
            @Param("category") String category,
            Pageable pageable // Spring injects this — contains page number, size, sort
    );
}
