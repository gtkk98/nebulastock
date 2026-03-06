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
    @Query(value = """ 
            SELECT * FROM stock_movements 
            WHERE(:from IS NULL OR moved_at >= CAST(:from AS timestamp))
            AND (:to IS NULL OR moved_at <= CAST(:to AS timestamp))
            ORDER BY moved_at DESC
           """,
            countQuery = """
            SELECT COUNT(*) FROM stock_movements
            WHERE (:from IS NULL OR moved_at >= CAST(:from AS timestamp))
            AND (:to IS NULL OR moved_at <= CAST(:to AS timestamp))
                """,
            nativeQuery = true)
    Page<StockMovement> findMovementHistory(
            @Param("from") String from,   // ← String not LocalDateTime
            @Param("to") String to,       // ← String not LocalDateTime
            Pageable pageable
    );
}
