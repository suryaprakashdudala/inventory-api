package com.inventory.service;

import java.util.Optional;
import com.inventory.entity.Inventory;

public interface InventoryService {
    Inventory stockIn(String productId, int quantity, String reason, String userId);
    Inventory stockOut(String productId, int quantity, String reason, String userId);
    Inventory adjustStock(String productId, int quantity, String reason, String userId);
    Inventory initializeInventory(String productId);
    Optional<Inventory> getInventoryByProductId(String productId);
}
