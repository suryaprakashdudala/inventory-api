package com.inventory.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.inventory.entity.StockMovement;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStats {
    private long totalProducts;
    private int totalAvailableQty;
    private long lowStockItemsCount;
    private long pendingPurchaseOrders;
    private long totalSuppliers;
    private double totalInventoryValue;
    
    private List<LowStockAlert> lowStockAlerts;
    private List<MovementActivity> recentMovements;
    private Map<String, Long> poStatusSummary;
    private List<MovementTrend> movementTrend;
    private List<ProductQtyStat> topProducts;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MovementActivity {
        private String id;
        private String productName;
        private String type;
        private int quantity;
        private String reason;
        private LocalDateTime timestamp;
        private String updatedBy;
    }

    @Data
    @AllArgsConstructor
    public static class LowStockAlert {
        private String sku;
        private String productName;
        private int availableQty;
        private int reorderLevel;
        private int shortageQty;
    }

    @Data
    @AllArgsConstructor
    public static class MovementTrend {
        private String date;
        private int inQty;
        private int outQty;
    }

    @Data
    @AllArgsConstructor
    public static class ProductQtyStat {
        private String name;
        private int quantity;
    }
}
