package com.nebulastock.nebulastock.service;

import com.nebulastock.nebulastock.dto.request.StockMovementRequest;
import com.nebulastock.nebulastock.dto.response.InventoryResponse;
import com.nebulastock.nebulastock.dto.response.StockMovementResponse;
import com.nebulastock.nebulastock.entity.Inventory;
import com.nebulastock.nebulastock.entity.Product;
import com.nebulastock.nebulastock.entity.StockMovement;
import com.nebulastock.nebulastock.entity.Warehouse;
import com.nebulastock.nebulastock.exception.ApiException;
import com.nebulastock.nebulastock.repository.InventoryRepository;
import com.nebulastock.nebulastock.repository.ProductRepository;
import com.nebulastock.nebulastock.repository.StockMovementRepository;
import com.nebulastock.nebulastock.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository  warehouseRepository;

    // STOCK MOVEMENT
    @Transactional // CRITICAL: if anything fails, ALL changes roll back
    public StockMovementResponse recordMovement(StockMovementRequest request) {
        // 1. Find product and warehouse (or throw 404)
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ApiException(
                        "Product not found: " + request.getProductId(), HttpStatus.NOT_FOUND));

        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ApiException(
                        "Warehouse not found: " + request.getWarehouseId(), HttpStatus.NOT_FOUND));

        // 2. Handle based on movement type
        switch (request.getMovementType()) {
            case IN -> handelStockIn(product, warehouse, request.getQuantity());
            case OUT -> handleStockOut(product, warehouse, request.getQuantity());
            case TRANSFER -> handleTransfer(product, warehouse, request);
        }

        // 3. Record the movement in audit trail
        StockMovement movement = StockMovement.builder()
                .product(product)
                .warehouse(warehouse)
                .movementType(request.getMovementType())
                .quantity(request.getQuantity())
                .build();

        return toMovementResponse(stockMovementRepository.save(movement));
    }

    // Add stock to warehouse
    private void handelStockIn(Product product, Warehouse warehouse, int quantity) {
        // Find existing inventory or create new record
        Inventory inventory = inventoryRepository
                .findByProductAndWarehouse(product, warehouse)
                .orElse(Inventory.builder()
                        .product(product)
                        .warehouse(warehouse)
                        .quantity(0)
                        .build());

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventoryRepository.save(inventory);

        }

    // Remove stock from warehouse
    private void handleStockOut(Product product, Warehouse warehouse, int quantity) {
        Inventory inventory = inventoryRepository
                .findByProductAndWarehouse(product, warehouse)
                .orElseThrow(() -> new ApiException(
                        "No inventory found for this product in this warehouse",
                        HttpStatus.NOT_FOUND));

        // Business Rule: Cannot go below Zero
        if (inventory.getQuantity() < quantity) {
            throw new ApiException(
                    "Insufficient stock! Available: " + inventory.getQuantity() +
                            ", Requested: " + quantity, HttpStatus.BAD_REQUEST);
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventoryRepository.save(inventory);
    }

    // Move stock from one warehouse to another
    private void handleTransfer(Product product, Warehouse fromWarehouse, StockMovementRequest request) {
        if (request.getDestinationWarehouseId() == null) {
            throw new ApiException(
            "Destination warehouse is required for TRANSFER",
            HttpStatus.BAD_REQUEST);
        }

        Warehouse toWarehouse = warehouseRepository
                .findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new ApiException(
                        "Destination warehouse not found", HttpStatus.NOT_FOUND));

        // take out from source
        handleStockOut(product, fromWarehouse, request.getQuantity());

        // Put into destination
        handelStockIn(product, toWarehouse, request.getQuantity());
    }

    // Read operation
    public List<InventoryResponse> getInventoryByWarehouse(Integer warehouseId) {
        Warehouse warehouse = warehouseRepository
                .findById(warehouseId)
                .orElseThrow(() -> new ApiException(
                        "Warehouse not found", HttpStatus.NOT_FOUND));
        return inventoryRepository.findByWarehouse(warehouse)
                .stream()
                .map(this::toInventoryResponse)
                .toList();
    }

    public List<InventoryResponse> getLowStock(int threshold) {
        return inventoryRepository.findLowStock(threshold)
                .stream()
                .map(this::toInventoryResponse)
                .toList();
    }

    public Page<StockMovementResponse> getMovementHistory(
            LocalDateTime from, LocalDateTime to, int page, int size) {
        String fromStr = from != null
                ? from.toString().replace('T', ' ')
                : "1970-01-01 00:00:00";
        String toStr = to != null
                ? to.toString().replace('T', ' ')
                : "9999-12-31 23:59:59";

        return stockMovementRepository.findMovementHistory(
                        fromStr, toStr,
                        PageRequest.of(page, size))
                .map(this::toMovementResponse);
    }

    // Mapper
    private InventoryResponse toInventoryResponse(Inventory i) {
        return InventoryResponse.builder()
                .id(i.getId())
                .productId(i.getProduct().getId())
                .productName(i.getProduct().getName())
                .productSku(i.getProduct().getSku())
                .warehouseId(i.getWarehouse().getId())
                .warehouseName(i.getWarehouse().getName())
                .quantity(i.getQuantity())
                .build();
    }

    private StockMovementResponse toMovementResponse(StockMovement m) {
        return StockMovementResponse.builder()
                .id(m.getId())
                .productName(m.getProduct().getName())
                .productSku(m.getProduct().getSku())
                .warehouseName(m.getWarehouse().getName())
                .movementType(m.getMovementType().name())
                .quantity(m.getQuantity())
                .referenceNote(m.getReferenceNote())
                .movedAt(m.getMovedAt())
                .build();
    }
}
