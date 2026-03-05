package com.nebulastock.nebulastock.repository;

import com.nebulastock.nebulastock.entity.StockMovement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, Integer> {
    // Get movement history with optional date range filtering
    @Query("SELECT s FROM StockMovement s WHERE " +
            "(:from IS NULL OR s.movedAt >= :from) AND " +
            " (:to IS NULL OR s.movedAt <= :to) " +
            "ORDER BY s.movedAt DESC")
    Page<StockMovement> findMovementHistory(
            @Param("from")LocalDateTime from,
            @Param("to")LocalDateTime to,
            Pageable pageable
    );
}
