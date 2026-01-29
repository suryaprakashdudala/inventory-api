package com.inventory.service;

import java.util.List;
import java.util.Optional;
import com.inventory.entity.PurchaseOrder;

public interface PurchaseOrderService {
    PurchaseOrder createOrder(PurchaseOrder order);
    PurchaseOrder updateOrderStatus(String id, String status, String userId);
    Optional<PurchaseOrder> getOrderById(String id);
    List<PurchaseOrder> getAllOrders();
}
