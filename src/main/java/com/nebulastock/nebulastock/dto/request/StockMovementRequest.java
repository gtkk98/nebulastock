package com.nebulastock.nebulastock.dto.request;

import com.nebulastock.nebulastock.entity.StockMovement;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockMovementRequest {
    @NotNull(message = "Product ID is required")
    private Integer productId;

    @NotNull(message = "Warehouse ID is required")
    private Integer warehouseId;

    @NotNull(message = "Movement type is required")
    private StockMovement.MovementType movementType; // IN, OUT, TRANSFER

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String referenceNote;

    //Only needed for TRANSFER - the destination warehouse
    private Integer destinationWarehouseId;
}
