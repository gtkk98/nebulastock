package com.nebulastock.nebulastock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockMovementResponse {
    private Integer id;
    private String productName;
    private String productSku;
    private String warehouseName;
    private String movementType;
    private Integer quantity;
    private String referenceNote;
    private LocalDateTime movedAt;
}
