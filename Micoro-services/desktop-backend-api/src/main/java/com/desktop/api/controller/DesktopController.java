package com.desktop.api.controller;

import com.desktop.api.entity.Desktop;
import com.desktop.api.repository.DesktopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/desktops")
public class DesktopController {

    @Autowired
    private DesktopRepository desktopRepository;

    @GetMapping
    public List<Desktop> getAllDesktops() {
        return desktopRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Desktop> getDesktopById(@PathVariable Long id) {
        return desktopRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Desktop createDesktop(@RequestBody Desktop desktop) {
        return desktopRepository.save(desktop);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Desktop> updateDesktop(@PathVariable Long id, @RequestBody Desktop desktopDetails) {
        return desktopRepository.findById(id)
                .map(existingDesktop -> {
                    existingDesktop.setBrand(desktopDetails.getBrand());
                    existingDesktop.setModel(desktopDetails.getModel());
                    existingDesktop.setProcessor(desktopDetails.getProcessor());
                    existingDesktop.setRam(desktopDetails.getRam());
                    existingDesktop.setStorage(desktopDetails.getStorage());
                    existingDesktop.setOperatingSystem(desktopDetails.getOperatingSystem());
                    existingDesktop.setGraphicsCard(desktopDetails.getGraphicsCard());
                    existingDesktop.setPrice(desktopDetails.getPrice());
                    return ResponseEntity.ok(desktopRepository.save(existingDesktop));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDesktop(@PathVariable Long id) {
        return desktopRepository.findById(id)
                .map(desktop -> {
                    desktopRepository.delete(desktop);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
