package com.nebulastock.nebulastock.repository;

import com.nebulastock.nebulastock.entity.Inventory;
import com.nebulastock.nebulastock.entity.Product;
import com.nebulastock.nebulastock.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    // Find stock level for a specific product in a specific warehouse
    Optional<Inventory> findByProductAndWarehouse(Product product, Warehouse warehouse);

    // Get all inventory for a specific warehouse
    List<Inventory> findByWarehouse(Warehouse warehouse);

    // Get all inventory for a specific product (across all warehouses)
    List<Inventory> findByProduct(Product product);

    // Custom query: find all inventory where quantity is below a threshold
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= :threshold")
    List<Inventory> findLowStock(int threshold);
}
