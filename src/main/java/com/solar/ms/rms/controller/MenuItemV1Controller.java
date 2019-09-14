package com.solar.ms.rms.controller;


import com.google.cloud.firestore.DocumentReference;
import com.solar.ms.rms.model.CreateMenuItemRequest;
import com.solar.ms.rms.service.MenuItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequestMapping("/v1/menuitem")
@RestController
public class MenuItemV1Controller {

    @Autowired
    private MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<?> createMenuItem(@RequestBody CreateMenuItemRequest request) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = menuItemService.insertMenuItem(request);

        return ResponseEntity.ok("OK");
    }
}
