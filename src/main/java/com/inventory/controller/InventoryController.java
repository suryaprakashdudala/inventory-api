package com.inventory.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventory.entity.Inventory;
import com.inventory.entity.StockMovement;
import com.inventory.repository.StockMovementRepo;
import com.inventory.service.InventoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;
    private final StockMovementRepo stockMovementRepo;

    @PostMapping("/stock-in")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<Inventory> stockIn(@RequestBody Map<String, Object> request) {
        String productId = (String) request.get("productId");
        int quantity = (int) request.get("quantity");
        String reason = (String) request.get("reason");
        String userId = (String) request.get("userId");
        return ResponseEntity.ok(inventoryService.stockIn(productId, quantity, reason, userId));
    }

    @PostMapping("/stock-out")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<Inventory> stockOut(@RequestBody Map<String, Object> request) {
        String productId = (String) request.get("productId");
        int quantity = (int) request.get("quantity");
        String reason = (String) request.get("reason");
        String userId = (String) request.get("userId");
        return ResponseEntity.ok(inventoryService.stockOut(productId, quantity, reason, userId));
    }

    @PostMapping("/adjust")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Inventory> adjustStock(@RequestBody Map<String, Object> request) {
        String productId = (String) request.get("productId");
        int quantity = (int) request.get("quantity");
        String reason = (String) request.get("reason");
        String userId = (String) request.get("userId");
        return ResponseEntity.ok(inventoryService.adjustStock(productId, quantity, reason, userId));
    }

    @GetMapping("/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF', 'VIEWER')")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable String productId) {
        return inventoryService.getInventoryByProductId(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.ok(inventoryService.initializeInventory(productId)));
    }

    @GetMapping("/history")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<StockMovement>> getAllHistory() {
        return ResponseEntity.ok(stockMovementRepo.findAll());
    }

    @GetMapping("/history/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'STAFF')")
    public ResponseEntity<List<StockMovement>> getHistory(@PathVariable String productId) {
        return ResponseEntity.ok(stockMovementRepo.findByProductId(productId));
    }
}
