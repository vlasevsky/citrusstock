package com.citrusmall.citrusstock.repository;

import com.citrusmall.citrusstock.model.Product;
import com.citrusmall.citrusstock.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
}
