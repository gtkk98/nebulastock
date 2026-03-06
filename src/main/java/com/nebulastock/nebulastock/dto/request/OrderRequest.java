package com.nebulastock.nebulastock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {

    @NotNull
    private Integer warehouseId;

    @NotBlank
    private String customerName;

    @NotEmpty
    private List<OrderItemRequest> items;
}
