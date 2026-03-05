package com.nebulastock.nebulastock.controller;

import com.nebulastock.nebulastock.dto.request.StockMovementRequest;
import com.nebulastock.nebulastock.dto.response.InventoryResponse;
import com.nebulastock.nebulastock.dto.response.StockMovementResponse;
import com.nebulastock.nebulastock.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    //Record a stock movement
    @PostMapping("/move")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<StockMovementResponse> recordMovement(
            @Valid @RequestBody StockMovementRequest request) {
        return ResponseEntity.ok(inventoryService.recordMovement(request));
    }

    // Get all current inventory levels
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getByWarehouse(
            @PathVariable int warehouseId) {
        return ResponseEntity.ok(inventoryService.getInventoryByWarehouse(warehouseId));
    }
    // Alert endpoint: products below threshold
    // Usage: /api/inventory/low-stock?threshold=10
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStock(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(inventoryService.getLowStock(threshold));
    }
    // Movement history with optional date range
    @GetMapping("/history")
    public ResponseEntity<Page<StockMovementResponse>> getHistory(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(
                inventoryService.getMovementHistory(from, to, page, size));
    }
}
