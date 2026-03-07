package com.nebulastock.nebulastock.service;

import com.nebulastock.nebulastock.dto.request.OrderItemRequest;
import com.nebulastock.nebulastock.dto.request.OrderRequest;
import com.nebulastock.nebulastock.dto.response.OrderItemResponse;
import com.nebulastock.nebulastock.dto.response.OrderResponse;
import com.nebulastock.nebulastock.entity.*;
import com.nebulastock.nebulastock.entity.Order.OrderStatus;
import com.nebulastock.nebulastock.exception.ApiException;
import com.nebulastock.nebulastock.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.nebulastock.nebulastock.repository.*;
import java.math.BigDecimal;



import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public OrderResponse placeOrder(OrderRequest request) {
        Warehouse warehouse =  warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new ApiException("Warehouse not found", HttpStatus.NOT_FOUND));

        // Build order
        Order order = Order.builder()
                .warehouse(warehouse)
                .customerName(request.getCustomerName())
                .build();
        order = orderRepository.save(order);

        List<OrderItem> items = new ArrayList<>();
        for (OrderItemRequest itemReq : request.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ApiException("Product not found" + itemReq.getProductId(), HttpStatus.NOT_FOUND));

            // Check and deduct the stock
            Inventory inventory = inventoryRepository.findByProductAndWarehouse(product, warehouse)
                    .orElseThrow(() -> new ApiException("No stock for product" + product.getName() + " in this warehouse", HttpStatus.BAD_REQUEST));

            if (inventory.getQuantity() < itemReq.getQuantity()) {
                throw new ApiException(
                        "Insufficient stock for: " + product.getName() + ". Available: " + inventory.getQuantity(), HttpStatus.BAD_REQUEST);
            }

            inventory.setQuantity(inventory.getQuantity() - itemReq.getQuantity());
            inventoryRepository.save(inventory);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemReq.getQuantity())
                    .unitPrice(product.getUnitPrice())
                    .build();
            items.add(item);
        }

        orderItemRepository.saveAll(items);
        order.setItems(items);
        return toResponse(order);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));

        validateStatusTransition(order.getStatus(), newStatus);

        // Restore stock if cancelled
        if (newStatus == OrderStatus.CANCELLED) {
            for (OrderItem orderItem : orderItemRepository.findByOrderId((orderId))) {
                Inventory inventory = inventoryRepository
                        .findByProductAndWarehouse(orderItem.getProduct(), order.getWarehouse())
                        .orElseThrow(() -> new ApiException("Inventory not found", HttpStatus.INTERNAL_SERVER_ERROR));
                inventory.setQuantity(inventory.getQuantity() + orderItem.getQuantity());
                inventoryRepository.save(inventory);
            }
        }

        order.setStatus(newStatus);
        order = orderRepository.save(order);
        order.setItems(orderItemRepository.findByOrderId(orderId));
        return toResponse(order);
    }

    public Page<OrderResponse> getAllOrders(int page, int size) {
        return orderRepository.findAll(PageRequest.of(page, size))
                .map(o -> {
                    o.setItems(orderItemRepository.findByOrderId(o.getId()));
                    return toResponse(o);
                });
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ApiException("Order not found", HttpStatus.NOT_FOUND));
        order.setItems(orderItemRepository.findByOrderId(id));
        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(o -> {
                    o.setItems(orderItemRepository.findByOrderId(o.getId()));
                    return toResponse(o);
                }).toList();
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PENDING -> next == OrderStatus.CONFIRMED || next == OrderStatus.CANCELLED;
            case CONFIRMED -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
        if (!valid) {
            throw new ApiException("Cannot transition from " + current + " to " + next, HttpStatus.BAD_REQUEST);
        }
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems() == null ? List.of() :
                order.getItems().stream().map(orderItem -> OrderItemResponse.builder()
                        .id(orderItem.getId())
                        .productId(orderItem.getProduct().getId())
                        .productName(orderItem.getProduct().getName())
                        .quantity(orderItem.getQuantity())
                        .unitPrice(orderItem.getUnitPrice())
                        .subtotal(orderItem.getUnitPrice()
                                .multiply(BigDecimal.valueOf(orderItem.getQuantity())))
                        .build()).toList();

        BigDecimal total = itemResponses.stream()
                .map(OrderItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return OrderResponse.builder()
                .id(order.getId())
                .warehouseId(order.getWarehouse().getId())
                .warehouseName(order.getWarehouse().getName())
                .customerName(order.getCustomerName())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(itemResponses)
                .totalAmount(total)
                .build();
    }
}
