package com.inventory.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Document(collection = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product extends BaseAuditEntity {
    @Id
    private String id;
    private String name;
    @Indexed(unique = true)
    private String sku;
    private String category;
    private int reorderLevel;
    private String status; // ACTIVE, INACTIVE, DISCONTINUED
    private int price;

}
