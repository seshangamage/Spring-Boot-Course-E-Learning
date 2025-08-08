package com.laptopapi.service;

import com.laptopapi.entity.Laptop;
import com.laptopapi.repository.LaptopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LaptopService {

    @Autowired
    private LaptopRepository laptopRepository;

    public List<Laptop> getAllLaptops() {
        return laptopRepository.findAll();
    }

    public Optional<Laptop> getLaptopById(Long id) {
        return laptopRepository.findById(id);
    }

    public Laptop saveLaptop(Laptop laptop) {
        return laptopRepository.save(laptop);
    }

    public void deleteLaptop(Long id) {
        laptopRepository.deleteById(id);
    }
}
