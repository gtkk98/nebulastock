package com.nebulastock.nebulastock.service;

import com.nebulastock.nebulastock.dto.request.WarehouseRequest;
import com.nebulastock.nebulastock.dto.response.WarehouseResponse;
import com.nebulastock.nebulastock.entity.Warehouse;
import com.nebulastock.nebulastock.exception.ApiException;
import com.nebulastock.nebulastock.repository.WarehouseRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Transactional
    public WarehouseResponse create(WarehouseRequest request) {
        if (warehouseRepository.existsByName(request.getName())) {
            throw new ApiException("Warehouse name already exists", HttpStatus.CONFLICT);
        }
        Warehouse warehouse = Warehouse.builder()
                .name(request.getName())
                .location(request.getLocation())
                .build();
        return toResponse(warehouseRepository.save(warehouse));
    }

    public List<WarehouseResponse> getAll() {
        return warehouseRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WarehouseResponse getById(Integer id) {
        return toResponse(findOrThrow(id));
    }

    @Transactional
    public WarehouseResponse update(Integer id, WarehouseRequest request) {
        Warehouse warehouse = findOrThrow(id);
        warehouse.setName(request.getName());
        warehouse.setLocation(request.getLocation());
        return toResponse(warehouseRepository.save(warehouse));
    }

    @Transactional
    public void delete(Integer id) {
        warehouseRepository.delete(findOrThrow(id));
    }

    private Warehouse findOrThrow(Integer id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Warehouse not found with id: " + id, HttpStatus.NOT_FOUND));
    }

    private WarehouseResponse toResponse(Warehouse w) {
        return WarehouseResponse.builder()
                .id(w.getId())
                .name(w.getName())
                .location(w.getLocation())
                .createdAt(w.getCreatedAt())
                .build();
    }
}
