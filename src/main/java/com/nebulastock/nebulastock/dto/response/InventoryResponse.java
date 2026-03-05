package com.nebulastock.nebulastock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productSku;
    private Integer warehouseId;
    private String warehouseName;
    private Integer quantity;
}
