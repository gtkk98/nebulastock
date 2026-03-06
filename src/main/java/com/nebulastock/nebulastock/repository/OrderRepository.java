package com.nebulastock.nebulastock.repository;

import com.nebulastock.nebulastock.entity.Order;
import com.nebulastock.nebulastock.entity.Order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAll(Pageable pageable);
    List<Order> findByStatus(OrderStatus status);
}
