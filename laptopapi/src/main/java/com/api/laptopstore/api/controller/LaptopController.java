package com.api.laptopstore.api.controller;

import com.api.laptopstore.api.model.Laptop;
import com.api.laptopstore.api.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laptops")
public class LaptopController {

    @Autowired
    private LaptopRepository laptopRepository;

    // CREATE - Add a new laptop
    @PostMapping
    public Laptop createLaptop(@RequestBody Laptop laptop) {
        return laptopRepository.save(laptop);
    }

    // READ - Get all laptops
    @GetMapping
    public List<Laptop> getAllLaptops() {
        return laptopRepository.findAll();
    }

    // READ - Get a single laptop by ID
    @GetMapping("/{id}")
    public ResponseEntity<Laptop> getLaptopById(@PathVariable Long id) {
        Laptop laptop = laptopRepository.findById(id)
            .orElse(null);
        if (laptop == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(laptop);
    }

    // UPDATE - Update a laptop
    @PutMapping("/{id}")
    public ResponseEntity<Laptop> updateLaptop(@PathVariable Long id, @RequestBody Laptop laptopDetails) {
        Laptop laptop = laptopRepository.findById(id)
            .orElse(null);
        if (laptop == null) {
            return ResponseEntity.notFound().build();
        }

        laptop.setBrand(laptopDetails.getBrand());
        laptop.setModel(laptopDetails.getModel());
        laptop.setProcessor(laptopDetails.getProcessor());
        laptop.setRam(laptopDetails.getRam());
        laptop.setStorage(laptopDetails.getStorage());
        laptop.setPrice(laptopDetails.getPrice());
        laptop.setDescription(laptopDetails.getDescription());

        Laptop updatedLaptop = laptopRepository.save(laptop);
        return ResponseEntity.ok(updatedLaptop);
    }

    // DELETE - Delete a laptop (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteLaptop(@PathVariable Long id) {
        Laptop laptop = laptopRepository.findById(id)
            .orElse(null);
        if (laptop == null) {
            return ResponseEntity.notFound().build();
        }

        laptopRepository.delete(laptop);
        return ResponseEntity.ok().build();
    }
}
