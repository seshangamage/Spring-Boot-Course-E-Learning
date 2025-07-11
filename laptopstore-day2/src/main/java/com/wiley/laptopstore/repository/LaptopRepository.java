package com.wiley.laptopstore.repository;

import com.wiley.laptopstore.entity.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {
    // Additional custom queries can be added here if needed
}
