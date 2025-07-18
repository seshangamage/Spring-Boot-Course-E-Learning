package com.example.laptopstore.api.controller;

import com.example.laptopstore.api.entity.Laptop;
import com.example.laptopstore.api.service.LaptopService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LaptopController.class)
class LaptopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LaptopService laptopService;

    @Autowired
    private ObjectMapper objectMapper;

    private Laptop testLaptop;

    @BeforeEach
    void setUp() {
        testLaptop = new Laptop();
        testLaptop.setId(1L);
        testLaptop.setBrand("Dell");
        testLaptop.setModel("XPS 13");
        testLaptop.setPrice(new BigDecimal("1299.99"));
        testLaptop.setDescription("Premium ultrabook");
        testLaptop.setProcessor("Intel Core i7");
        testLaptop.setRamSizeGB(16);
        testLaptop.setStorageSizeGB(512);
        testLaptop.setStorageType("SSD");
        testLaptop.setScreenSize("13.3 inches");
    }

    @Test
    void getAllLaptops_ShouldReturnListOfLaptops() throws Exception {
        when(laptopService.getAllLaptops()).thenReturn(Arrays.asList(testLaptop));

        mockMvc.perform(get("/api/laptops"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].brand").value("Dell"))
                .andExpect(jsonPath("$[0].model").value("XPS 13"));
    }

    @Test
    void getLaptopById_WhenExists_ShouldReturnLaptop() throws Exception {
        when(laptopService.getLaptopById(1L)).thenReturn(Optional.of(testLaptop));

        mockMvc.perform(get("/api/laptops/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.brand").value("Dell"))
                .andExpect(jsonPath("$.model").value("XPS 13"));
    }

    @Test
    void getLaptopById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        when(laptopService.getLaptopById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/laptops/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createLaptop_WithValidData_ShouldReturnCreatedLaptop() throws Exception {
        when(laptopService.createLaptop(any(Laptop.class))).thenReturn(testLaptop);

        mockMvc.perform(post("/api/laptops")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLaptop)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.brand").value("Dell"));
    }

    @Test
    void deleteLaptop_WhenExists_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/laptops/1"))
                .andExpect(status().isNoContent());
    }
}
