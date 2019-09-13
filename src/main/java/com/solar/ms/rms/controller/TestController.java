package com.solar.ms.rms.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Slf4j
@RefreshScope
@RestController
public class TestController {

    @Value("${application-test:default}")
    public String appTestProp;
    @Value("${rms-ms-test:default}")
    public String rmsTestProp;
    @Value("${application-secret:default}")
    public String secretProp;

    @Autowired
    private Firestore firestore;

    @PostMapping("/v1/test")
    public ResponseEntity<String> testEndpoint(@RequestBody String payload) throws ExecutionException, InterruptedException {
        log.info("{}", payload);
        return ResponseEntity.ok(payload);
    }

    @PostMapping("/v1/firebase")
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

    @GetMapping("/v1/config")
    public ResponseEntity<?> testConfig(){
        Map<String, String> data = new HashMap<>();
        data.put("app", appTestProp);
        data.put("rms", rmsTestProp);

        return ResponseEntity.ok(data);
    }

    @GetMapping("/v1/secret")
    public ResponseEntity<?> testSecret(){
        Map<String, String> data = new HashMap<>();
        data.put("secret", secretProp);

        return ResponseEntity.ok(data);
    }
}
