package com.solar.ms.rms.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
public class TestController {

    @Autowired
    private Firestore firestore;

    @PostMapping("/v1/test")
    public ResponseEntity<String> testEndpoint(@RequestBody String payload) throws ExecutionException, InterruptedException {
        log.info("{}", payload);
        return ResponseEntity.ok(payload);
    }

    @PostMapping("/v1/test")
    public ResponseEntity<String> testFireBase(@RequestBody String payload) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection("users").document("alovelace");
        // Add document data  with id "alovelace" using a hashmap
        Map<String, Object> data = new HashMap<>();
        data.put("first", payload);
        data.put("last", payload);
        data.put("born", 1815);
        //asynchronously write data
        ApiFuture<WriteResult> result = docRef.set(data);
        // ...
        // result.get() blocks on response
        System.out.println("Update time : " + result.get().getUpdateTime());

        log.info("{}", payload);
        return ResponseEntity.ok(payload);
    }
}
