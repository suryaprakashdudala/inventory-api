package com.inventory.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventory.entity.PurchaseOrder;
import com.inventory.entity.PurchaseOrderItem;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.PurchaseOrderRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepo purchaseOrderRepo;
    private final InventoryService inventoryService;
    private final SequenceGeneratorService sequenceGeneratorService;

    @Override
    public PurchaseOrder createOrder(PurchaseOrder order) {
        log.info("Creating purchase order for supplier: {}", order.getSupplierId());
        
        // Generate sequential order number
        long seq = sequenceGeneratorService.generateSequence("purchase_orders_sequence");
        order.setOrderNumber("PO-" + seq);
        
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        return purchaseOrderRepo.save(order);
    }

    @Override
    @Transactional
    public PurchaseOrder updateOrderStatus(String id, String status, String userId) {
        log.info("Updating purchase order {} status to {}", id, status);
        PurchaseOrder order = purchaseOrderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Purchase order not found with id: " + id));

        String oldStatus = order.getStatus();
        order.setStatus(status);
        order.setUpdatedAt(LocalDateTime.now());
        
        PurchaseOrder updatedOrder = purchaseOrderRepo.save(order);

        // If status changes to RECEIVED, stock in all items
        if ("RECEIVED".equals(status) && !"RECEIVED".equals(oldStatus)) {
            for (PurchaseOrderItem item : order.getItems()) {
                inventoryService.stockIn(item.getProductId(), item.getQuantity(), "Purchase Order Received: " + order.getOrderNumber(), userId);
            }
        }

        return updatedOrder;
    }

    @Override
    public Optional<PurchaseOrder> getOrderById(String id) {
        return purchaseOrderRepo.findById(id);
    }

    @Override
    public List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepo.findAll();
    }
}
