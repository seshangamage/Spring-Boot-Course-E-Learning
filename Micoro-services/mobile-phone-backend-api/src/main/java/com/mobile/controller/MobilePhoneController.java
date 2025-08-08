package com.mobile.controller;

import com.mobile.entity.MobilePhone;
import com.mobile.repository.MobilePhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/phones")
public class MobilePhoneController {

    @Autowired
    private MobilePhoneRepository repository;

    @GetMapping
    public List<MobilePhone> getAllPhones() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MobilePhone> getPhoneById(@PathVariable Long id) {
        Optional<MobilePhone> phone = repository.findById(id);
        return phone.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public MobilePhone createPhone(@RequestBody MobilePhone phone) {
        return repository.save(phone);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MobilePhone> updatePhone(@PathVariable Long id, @RequestBody MobilePhone phone) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        phone.setId(id);
        return ResponseEntity.ok(repository.save(phone));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePhone(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
