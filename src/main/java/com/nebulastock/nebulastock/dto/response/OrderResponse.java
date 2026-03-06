package com.nebulastock.nebulastock.dto.response;
import com.nebulastock.nebulastock.entity.Order.OrderStatus;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Integer id;
    private Integer warehouseId;
    private String warehouseName;
    private String customerName;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponse> items;
    private Double totalAmount;
}
