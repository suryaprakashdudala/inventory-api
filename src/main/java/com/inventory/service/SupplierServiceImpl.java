package com.inventory.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.inventory.entity.Supplier;
import com.inventory.exception.ResourceNotFoundException;
import com.inventory.repository.SupplierRepo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepo supplierRepo;

    @Override
    public Supplier addSupplier(Supplier supplier) {
        log.info("Adding new supplier: {}", supplier.getName());
        return supplierRepo.save(supplier);
    }

    @Override
    public Supplier updateSupplier(String id, Supplier supplier) {
        log.info("Updating supplier with ID: {}", id);
        Supplier existingSupplier = supplierRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        existingSupplier.setName(supplier.getName());
        existingSupplier.setEmail(supplier.getEmail());
        existingSupplier.setPhone(supplier.getPhone());
        existingSupplier.setAddress(supplier.getAddress());
        existingSupplier.setProductsSupplied(supplier.getProductsSupplied());

        return supplierRepo.save(existingSupplier);
    }

    @Override
    public Optional<Supplier> getSupplierById(String id) {
        return supplierRepo.findById(id);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepo.findAll();
    }

    @Override
    public void deleteSupplier(String id) {
        log.info("Deleting supplier with ID: {}", id);
        if (!supplierRepo.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
        supplierRepo.deleteById(id);
    }
}
