package com.laptopapi.controller;

import com.laptopapi.entity.Laptop;
import com.laptopapi.service.LaptopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laptops")
public class LaptopController {

    @Autowired
    private LaptopService laptopService;

    @GetMapping
    public List<Laptop> getAllLaptops() {
        return laptopService.getAllLaptops();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Laptop> getLaptopById(@PathVariable Long id) {
        return laptopService.getLaptopById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Laptop createLaptop(@RequestBody Laptop laptop) {
        return laptopService.saveLaptop(laptop);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLaptop(@PathVariable Long id) {
        laptopService.deleteLaptop(id);
        return ResponseEntity.ok().build();
    }
}
