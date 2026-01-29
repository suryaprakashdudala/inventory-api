package com.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inventory.entity.Product;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.ProductRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final InventoryService inventoryService;

    @Override
    public Product addProduct(Product product) {
        log.info("Adding new product with SKU: {}", product.getSku());
        if (productRepo.findBySku(product.getSku()).isPresent()) {
            throw new IllegalArgumentException("Product with SKU " + product.getSku() + " already exists");
        }
        Product savedProduct = productRepo.save(product);
        inventoryService.initializeInventory(savedProduct.getId());
        return savedProduct;
    }

    @Override
    public Product updateProduct(String id, Product product) {
        log.info("Updating product with ID: {}", id);
        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        existingProduct.setName(product.getName());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setReorderLevel(product.getReorderLevel());
        existingProduct.setStatus(product.getStatus());
        // SKU is usually not updated to maintain consistency, but can be added if needed

        return productRepo.save(existingProduct);
    }

    @Override
    public Optional<Product> getProductById(String id) {
        return productRepo.findById(id);
    }

    @Override
    public Optional<Product> getProductBySku(String sku) {
        return productRepo.findBySku(sku);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    @Override
    public void deleteProduct(String id) {
        log.info("Deleting product with ID: {}", id);
        if (!productRepo.existsById(id)) {
            throw new ResourceNotFoundException("Product not found with id: " + id);
        }
        productRepo.deleteById(id);
    }
}
