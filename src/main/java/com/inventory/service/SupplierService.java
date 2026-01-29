package com.inventory.service;

import java.util.List;
import java.util.Optional;
import com.inventory.entity.Supplier;

public interface SupplierService {
    Supplier addSupplier(Supplier supplier);
    Supplier updateSupplier(String id, Supplier supplier);
    Optional<Supplier> getSupplierById(String id);
    List<Supplier> getAllSuppliers();
    void deleteSupplier(String id);
}
