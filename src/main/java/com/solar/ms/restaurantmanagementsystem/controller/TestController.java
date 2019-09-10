package com.solar.ms.restaurantmanagementsystem.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class TestController {

    @PostMapping("/v1/test")
    public ResponseEntity<String> testEndpoint(@RequestBody String payload){
        log.info("{}", payload);
        return ResponseEntity.ok(payload);
    }
}
