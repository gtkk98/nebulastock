package com.nebulastock.nebulastock.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class WarehouseRequest {
    @NotBlank(message = "Warehouse name is required")
    @Size(max = 100, message = "Name must be under 100 characters")
    private String name;

    @Size(max = 200, message = "Location must be under 200 characters")
    private String location;
}
