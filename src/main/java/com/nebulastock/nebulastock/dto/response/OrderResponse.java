package com.nebulastock.nebulastock.dto.response;
import com.nebulastock.nebulastock.entity.Order.OrderStatus;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long warehouseId;
    private String warehouseName;
    private String customerName;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;
    private BigDecimal totalAmount;
}
