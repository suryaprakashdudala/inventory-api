package com.inventory.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.entity.Inventory;
import com.inventory.entity.StockMovement;
import com.inventory.entity.Product;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.InventoryRepo;
import com.inventory.repository.ProductRepo;
import com.inventory.repository.StockMovementRepo;
import com.inventory.repository.UserRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepo inventoryRepo;
    private final StockMovementRepo stockMovementRepo;
    private final ProductRepo productRepo;
    private final UserRepo userRepo;
    private final EmailService emailService;

    @Override
    @Transactional
    public Inventory stockIn(String productId, int quantity, String reason, String userId) {
        log.info("Stock In: Product {}, Quantity {}, Reason: {}", productId, quantity, reason);
        
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        
        if ("DISCONTINUED".equals(product.getStatus())) {
            throw new IllegalArgumentException("Cannot stock in a DISCONTINUED product");
        }

        Inventory inventory = inventoryRepo.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .availableQty(0)
                        .reservedQty(0)
                        .build());

        inventory.setAvailableQty(inventory.getAvailableQty() + quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        inventory = inventoryRepo.save(inventory);

        logStockMovement(productId, "IN", quantity, reason, userId);
        return inventory;
    }

    @Override
    @Transactional
    public Inventory stockOut(String productId, int quantity, String reason, String userId) {
        log.info("Stock Out: Product {}, Quantity {}, Reason: {}", productId, quantity, reason);
        
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        
        if ("INACTIVE".equals(product.getStatus()) || "DISCONTINUED".equals(product.getStatus())) {
            throw new IllegalArgumentException("Cannot stock out product with status: " + product.getStatus());
        }

        Inventory inventory = inventoryRepo.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getAvailableQty() < quantity) {
            throw new IllegalArgumentException("Insufficient stock for product: " + productId);
        }

        inventory.setAvailableQty(inventory.getAvailableQty() - quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        inventory = inventoryRepo.save(inventory);

        logStockMovement(productId, "OUT", quantity, reason, userId);
        
        checkAndNotifyLowStock(product, inventory); // Call new method

        return inventory;
    }

    @Override
    @Transactional
    public Inventory adjustStock(String productId, int quantity, String reason, String userId) {
        log.info("Adjust Stock: Product {}, New Quantity {}, Reason: {}", productId, quantity, reason);
        
        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
        
        if ("DISCONTINUED".equals(product.getStatus())) {
            throw new IllegalArgumentException("Cannot adjust stock for a DISCONTINUED product");
        }

        Inventory inventory = inventoryRepo.findByProductId(productId)
                .orElse(Inventory.builder()
                        .productId(productId)
                        .reservedQty(0)
                        .build());

        int diff = quantity - inventory.getAvailableQty();
        inventory.setAvailableQty(quantity);
        inventory.setLastUpdated(LocalDateTime.now());
        inventory = inventoryRepo.save(inventory);

        logStockMovement(productId, "ADJUST", diff, reason, userId);

        checkAndNotifyLowStock(product, inventory);

        return inventory;
    }

    private void checkAndNotifyLowStock(Product product, Inventory inventory) {
        if (inventory.getAvailableQty() < product.getReorderLevel()) {
            log.warn("REORDER ALERT: Product {} ({}) is below reorder level ({}). Current stock: {}", 
                     product.getName(), product.getId(), product.getReorderLevel(), inventory.getAvailableQty());
            
            userRepo.findAllByRole(com.inventory.entity.Role.ADMIN).forEach(admin -> {
                try {
                    emailService.sendLowStockAlert(
                        admin.getEmail(), 
                        product.getName(), 
                        product.getSku(), 
                        inventory.getAvailableQty(), 
                        product.getReorderLevel(),
                        admin.getUserName()
                    );
                } catch (Exception e) {
                    log.error("Failed to send low stock alert to admin: {}", admin.getEmail(), e);
                }
            });
        }
    }

    @Override
    @Transactional
    public Inventory initializeInventory(String productId) {
        log.info("Initializing inventory for product: {}", productId);
        return inventoryRepo.findByProductId(productId)
                .orElseGet(() -> inventoryRepo.save(Inventory.builder()
                        .productId(productId)
                        .availableQty(0)
                        .reservedQty(0)
                        .lastUpdated(LocalDateTime.now())
                        .build()));
    }

    @Override
    public Optional<Inventory> getInventoryByProductId(String productId) {
        return inventoryRepo.findByProductId(productId);
    }

    private void logStockMovement(String productId, String type, int quantity, String reason, String userId) {
        StockMovement movement = StockMovement.builder()
                .productId(productId)
                .type(type)
                .quantity(quantity)
                .reason(reason)
                .updatedBy(userId)
                .createdBy(userId)
                .timestamp(LocalDateTime.now())
                .build();
        stockMovementRepo.save(movement);
    }
}
