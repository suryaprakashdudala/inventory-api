package com.inventory.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Document(collection = "stock_movements")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class StockMovement extends BaseAuditEntity {

    @Id
    private String id;

    private String productId;

    private String type;

    private int quantity;

    private String reason;

    private LocalDateTime timestamp;
}
