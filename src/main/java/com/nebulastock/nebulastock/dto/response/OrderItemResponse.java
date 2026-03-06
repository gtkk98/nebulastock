package com.nebulastock.nebulastock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Double unitPrice;
    private Double subtotal;
}
