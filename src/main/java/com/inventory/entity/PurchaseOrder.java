package com.inventory.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "purchase_orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {

    @Id
    private String id;

    private String supplierId;

    private String orderNumber;

    private String status; // CREATED, APPROVED, RECEIVED, CANCELLED

    private List<PurchaseOrderItem> items;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
