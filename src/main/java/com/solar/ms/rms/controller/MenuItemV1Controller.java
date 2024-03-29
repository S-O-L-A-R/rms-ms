package com.solar.ms.rms.controller;


import com.google.cloud.firestore.DocumentReference;
import com.solar.ms.rms.model.CreateMenuItemRequest;
import com.solar.ms.rms.model.RemoveMenuItemRequest;
import com.solar.ms.rms.service.MenuItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Slf4j
@RequestMapping("/v1/menuitem")
@RestController
public class MenuItemV1Controller {

    @Autowired
    private MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<?> addMenuItem(@RequestBody CreateMenuItemRequest request) throws ExecutionException, InterruptedException {
        DocumentReference documentReference = menuItemService.addMenuItem(request);

        return ResponseEntity.ok(documentReference.getPath());
    }

    @DeleteMapping
    public ResponseEntity<?> removeMenuItem(@RequestBody RemoveMenuItemRequest request) throws ExecutionException, InterruptedException {
        int documentReference = menuItemService.removeMenuItem(request);

        return ResponseEntity.ok(documentReference);
    }}
