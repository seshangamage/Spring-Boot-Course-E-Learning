package com.example.laptopstore.api.repository;

import com.example.laptopstore.api.entity.Laptop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LaptopRepository extends JpaRepository<Laptop, Long> {
    
    // Find laptops by brand (case-insensitive)
    List<Laptop> findByBrandIgnoreCase(String brand);
    
    // Find laptops by model containing a keyword (case-insensitive)
    List<Laptop> findByModelContainingIgnoreCase(String model);
    
    // Find laptops within a price range
    List<Laptop> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    // Find laptops by RAM size
    List<Laptop> findByRamSizeGB(Integer ramSize);
    
    // Find laptops by storage size greater than or equal to specified value
    List<Laptop> findByStorageSizeGBGreaterThanEqual(Integer storageSize);
    
    // Find laptops by processor containing keyword (case-insensitive)
    List<Laptop> findByProcessorContainingIgnoreCase(String processor);
    
    // Custom query to find laptops by multiple criteria
    @Query("SELECT l FROM Laptop l WHERE " +
           "(:brand IS NULL OR LOWER(l.brand) LIKE LOWER(CONCAT('%', :brand, '%'))) AND " +
           "(:minPrice IS NULL OR l.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR l.price <= :maxPrice) AND " +
           "(:minRam IS NULL OR l.ramSizeGB >= :minRam) AND " +
           "(:minStorage IS NULL OR l.storageSizeGB >= :minStorage)")
    List<Laptop> findLaptopsWithFilters(
            @Param("brand") String brand,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("minRam") Integer minRam,
            @Param("minStorage") Integer minStorage
    );
    
    // Check if a laptop with same brand and model exists
    boolean existsByBrandAndModel(String brand, String model);
}
