package com.inventory.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.inventory.dto.DashboardStats;
import com.inventory.entity.Inventory;
import com.inventory.entity.Product;
import com.inventory.entity.PurchaseOrder;
import com.inventory.entity.StockMovement;
import com.inventory.repository.InventoryRepo;
import com.inventory.repository.ProductRepo;
import com.inventory.repository.PurchaseOrderRepo;
import com.inventory.repository.StockMovementRepo;
import com.inventory.repository.SupplierRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ProductRepo productRepo;
    private final InventoryRepo inventoryRepo;
    private final SupplierRepo supplierRepo;
    private final PurchaseOrderRepo purchaseOrderRepo;
    private final StockMovementRepo stockMovementRepo;

    @Override
    public DashboardStats getDashboardStats() {
        List<Product> products = productRepo.findAll();
        List<Inventory> inventories = inventoryRepo.findAll();
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepo.findAll();
        
        Map<String, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
        
        Map<String, Inventory> inventoryMap = inventories.stream()
                .collect(Collectors.toMap(Inventory::getProductId, Function.identity()));

        // 1. KPI Calculations
        long totalProducts = products.size();
        int totalAvailableQty = inventories.stream().mapToInt(Inventory::getAvailableQty).sum();
        long totalSuppliers = supplierRepo.count();
        
        long lowStockCount = products.stream()
                .filter(p -> {
                    Inventory inv = inventoryMap.get(p.getId());
                    return inv != null && inv.getAvailableQty() <= p.getReorderLevel();
                }).count();

        long pendingPOs = purchaseOrders.stream()
                .filter(po -> "CREATED".equals(po.getStatus()))
                .count();

        double totalInventoryValue = products.stream()
                .mapToDouble(p -> {
                    Inventory inv = inventoryMap.get(p.getId());
                    return inv != null ? p.getPrice() * inv.getAvailableQty() : 0.0;
                }).sum();

        // 2. Low Stock Alerts
        List<DashboardStats.LowStockAlert> lowStockAlerts = products.stream()
                .filter(p -> {
                    Inventory inv = inventoryMap.get(p.getId());
                    return inv != null && inv.getAvailableQty() <= p.getReorderLevel();
                })
                .map(p -> {
                    Inventory inv = inventoryMap.get(p.getId());
                    int shortage = p.getReorderLevel() - inv.getAvailableQty();
                    return new DashboardStats.LowStockAlert(p.getSku(), p.getName(), 
                            inv.getAvailableQty(), p.getReorderLevel(), Math.max(0, shortage));
                })
                .limit(5)
                .collect(Collectors.toList());

        // 3. Recent Stock Movements (Limited to 5 for height consistency)
        List<DashboardStats.MovementActivity> recentMovements = stockMovementRepo.findAll(Sort.by(Sort.Direction.DESC, "timestamp"))
                .stream().limit(5)
                .map(m -> {
                    Product p = productMap.get(m.getProductId());
                    return DashboardStats.MovementActivity.builder()
                            .id(m.getId())
                            .productName(p != null ? p.getName() : "Unknown Product")
                            .type(m.getType())
                            .quantity(m.getQuantity())
                            .reason(m.getReason())
                            .timestamp(m.getTimestamp())
                            .updatedBy(m.getUpdatedBy())
                            .build();
                })
                .collect(Collectors.toList());

        // 4. PO Status Summary
        Map<String, Long> poStatusSummary = purchaseOrders.stream()
                .collect(Collectors.groupingBy(PurchaseOrder::getStatus, Collectors.counting()));

        // 5. Movement Trend (Last 7 Days)
        List<DashboardStats.MovementTrend> movementTrend = calculateMovementTrend();

        // 6. Top Products by Quantity
        List<DashboardStats.ProductQtyStat> topProducts = inventories.stream()
                .sorted((a, b) -> b.getAvailableQty() - a.getAvailableQty())
                .limit(5)
                .map(inv -> {
                    Product p = productMap.get(inv.getProductId());
                    return new DashboardStats.ProductQtyStat(p != null ? p.getName() : "Unknown", inv.getAvailableQty());
                })
                .collect(Collectors.toList());

        return DashboardStats.builder()
                .totalProducts(totalProducts)
                .totalAvailableQty(totalAvailableQty)
                .lowStockItemsCount(lowStockCount)
                .pendingPurchaseOrders(pendingPOs)
                .totalSuppliers(totalSuppliers)
                .totalInventoryValue(totalInventoryValue)
                .lowStockAlerts(lowStockAlerts)
                .recentMovements(recentMovements)
                .poStatusSummary(poStatusSummary)
                .movementTrend(movementTrend)
                .topProducts(topProducts)
                .build();
    }

    private List<DashboardStats.MovementTrend> calculateMovementTrend() {
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        List<StockMovement> recentMovements = stockMovementRepo.findAll().stream()
                .filter(m -> m.getTimestamp().isAfter(sevenDaysAgo))
                .collect(Collectors.toList());

        Map<String, DashboardStats.MovementTrend> trendMap = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd");

        for (int i = 6; i >= 0; i--) {
            String date = LocalDate.now().minusDays(i).format(formatter);
            trendMap.put(date, new DashboardStats.MovementTrend(date, 0, 0));
        }

        for (StockMovement m : recentMovements) {
            String date = m.getTimestamp().format(formatter);
            if (trendMap.containsKey(date)) {
                DashboardStats.MovementTrend trend = trendMap.get(date);
                if ("IN".equals(m.getType())) {
                    trend.setInQty(trend.getInQty() + m.getQuantity());
                } else if ("OUT".equals(m.getType())) {
                    trend.setOutQty(trend.getOutQty() + m.getQuantity());
                }
            }
        }

        return new ArrayList<>(trendMap.values());
    }
}
