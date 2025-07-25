package com.api.laptopstore.api.repository;

import com.api.laptopstore.api.model.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository
}
