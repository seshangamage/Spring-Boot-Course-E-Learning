package com.example.laptopstore.api.controller;

import com.example.laptopstore.api.entity.Laptop;
import com.example.laptopstore.api.service.LaptopService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/laptops")
public class LaptopController {
    
    private final LaptopService laptopService;
    
    @Autowired
    public LaptopController(LaptopService laptopService) {
        this.laptopService = laptopService;
    }
    
    /**
     * CREATE - Add a new laptop
     * POST /api/laptops
     * Only ADMIN and MODERATOR roles can create laptops
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> createLaptop(@Valid @RequestBody Laptop laptop) {
        try {
            Laptop createdLaptop = laptopService.createLaptop(laptop);
            return new ResponseEntity<>(createdLaptop, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating laptop: " + e.getMessage());
        }
    }
    
    /**
     * READ - Get all laptops
     * GET /api/laptops
     */
    @GetMapping
    public ResponseEntity<List<Laptop>> getAllLaptops() {
        try {
            List<Laptop> laptops = laptopService.getAllLaptops();
            return new ResponseEntity<>(laptops, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * READ - Get laptop by ID
     * GET /api/laptops/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getLaptopById(@PathVariable Long id) {
        try {
            Optional<Laptop> laptop = laptopService.getLaptopById(id);
            if (laptop.isPresent()) {
                return new ResponseEntity<>(laptop.get(), HttpStatus.OK);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Laptop not found with id: " + id);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving laptop: " + e.getMessage());
        }
    }
    
    /**
     * UPDATE - Update an existing laptop
     * PUT /api/laptops/{id}
     * Only ADMIN and MODERATOR roles can update laptops
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MODERATOR')")
    public ResponseEntity<?> updateLaptop(@PathVariable Long id, @Valid @RequestBody Laptop laptopDetails) {
        try {
            Laptop updatedLaptop = laptopService.updateLaptop(id, laptopDetails);
            return new ResponseEntity<>(updatedLaptop, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Error: " + e.getMessage());
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Error: " + e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating laptop: " + e.getMessage());
        }
    }
    
    /**
     * DELETE - Delete a laptop
     * DELETE /api/laptops/{id}
     * Only ADMIN role can delete laptops
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteLaptop(@PathVariable Long id) {
        try {
            laptopService.deleteLaptop(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT)
                    .body("Laptop deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting laptop: " + e.getMessage());
        }
    }
    
    // Additional search endpoints
    
    /**
     * Search laptops by brand
     * GET /api/laptops/search/brand/{brand}
     */
    @GetMapping("/search/brand/{brand}")
    public ResponseEntity<List<Laptop>> getLaptopsByBrand(@PathVariable String brand) {
        try {
            List<Laptop> laptops = laptopService.getLaptopsByBrand(brand);
            return new ResponseEntity<>(laptops, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Search laptops by model keyword
     * GET /api/laptops/search/model?q={keyword}
     */
    @GetMapping("/search/model")
    public ResponseEntity<List<Laptop>> getLaptopsByModel(@RequestParam("q") String model) {
        try {
            List<Laptop> laptops = laptopService.getLaptopsByModel(model);
            return new ResponseEntity<>(laptops, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Search laptops by price range
     * GET /api/laptops/search/price?min={minPrice}&max={maxPrice}
     */
    @GetMapping("/search/price")
    public ResponseEntity<List<Laptop>> getLaptopsByPriceRange(
            @RequestParam("min") BigDecimal minPrice,
            @RequestParam("max") BigDecimal maxPrice) {
        try {
            List<Laptop> laptops = laptopService.getLaptopsByPriceRange(minPrice, maxPrice);
            return new ResponseEntity<>(laptops, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Search laptops by RAM size
     * GET /api/laptops/search/ram/{ramSize}
     */
    @GetMapping("/search/ram/{ramSize}")
    public ResponseEntity<List<Laptop>> getLaptopsByRamSize(@PathVariable Integer ramSize) {
        try {
            List<Laptop> laptops = laptopService.getLaptopsByRamSize(ramSize);
            return new ResponseEntity<>(laptops, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Search laptops by minimum storage size
     * GET /api/laptops/search/storage?min={storageSize}
     */
    @GetMapping("/search/storage")
    public ResponseEntity<List<Laptop>> getLaptopsByMinStorageSize(@RequestParam("min") Integer storageSize) {
        try {
            List<Laptop> laptops = laptopService.getLaptopsByMinStorageSize(storageSize);
            return new ResponseEntity<>(laptops, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Search laptops by processor keyword
     * GET /api/laptops/search/processor?q={keyword}
     */
    @GetMapping("/search/processor")
    public ResponseEntity<List<Laptop>> getLaptopsByProcessor(@RequestParam("q") String processor) {
        try {
            List<Laptop> laptops = laptopService.getLaptopsByProcessor(processor);
            return new ResponseEntity<>(laptops, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Advanced search with multiple filters
     * GET /api/laptops/search?brand={brand}&minPrice={minPrice}&maxPrice={maxPrice}&minRam={minRam}&minStorage={minStorage}
     */
    @GetMapping("/search")
    public ResponseEntity<List<Laptop>> searchLaptops(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer minRam,
            @RequestParam(required = false) Integer minStorage) {
        try {
            List<Laptop> laptops = laptopService.searchLaptops(brand, minPrice, maxPrice, minRam, minStorage);
            return new ResponseEntity<>(laptops, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
