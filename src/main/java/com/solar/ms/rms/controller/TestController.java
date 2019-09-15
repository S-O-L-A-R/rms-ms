package com.solar.ms.rms.controller;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.StorageClient;
import com.solar.ms.rms.model.line.LineMessage;
import com.solar.ms.rms.model.line.LineMessageRequest;
import com.solar.ms.rms.service.LineMessageService;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
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
    @Autowired
    private StorageClient storageClient;
    @Autowired
    private LineMessageService lineMessageService;

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

    @PostMapping("/v1/upload")
    public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {

        log.info("{}", file.getOriginalFilename());

        storageClient.bucket().create(file.getOriginalFilename(), file.getInputStream());

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/v1/message/broadcast")
    public ResponseEntity<String> sendBroadcastMessage(@RequestBody List<LineMessage> messages){
        lineMessageService.sendBroadCastMessage(messages);

        return ResponseEntity.ok("OK");
    }

    @PostMapping("/v1/message/push")
    public ResponseEntity<String> sendBroadcastMessage(@RequestBody LineMessageRequest lineMessageRequest){
        lineMessageService.sendPushMessage(lineMessageRequest.getTo(), lineMessageRequest.getMessages());

        return ResponseEntity.ok("OK");
    }
}
