package com.solar.ms.restaurantmanagementsystem.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @PostMapping("/v1/test")
    public ResponseEntity<String> testEndpoint(String payload){
        return ResponseEntity.ok(payload);
    }
}
