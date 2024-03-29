package com.solar.ms.rms.config;

import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.*;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id}")
    private String projectId;

    @Value("${firebase.storage-bucket}")
    private String storageBucket;

    @Bean(name = "firebaseApp")
    @Profile("local")
    public FirebaseApp getFirebaseAppLocal() throws IOException {
        InputStream serviceAccount = new FileInputStream(ResourceUtils.getFile("classpath:keys/firebase-sa.json"));
        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .setStorageBucket(storageBucket)
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean(name = "firebaseApp")
    @Profile("deployment")
    public FirebaseApp getFirebaseAppDeployment() {
        GoogleCredentials credentials = GoogleCredentials.create(new AccessToken("MOCK", new Date()));

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .setStorageBucket(storageBucket)
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean(name = "firebaseApp")
    @ConditionalOnMissingBean
    public FirebaseApp getFirebaseApp() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .setStorageBucket(storageBucket)
                .build();
        return FirebaseApp.initializeApp(options);
    }

    @Bean
    @DependsOn(value = "firebaseApp")
    public Firestore getFirestore(){
        return FirestoreClient.getFirestore();
    }

    @Bean
    @DependsOn(value = "firebaseApp")
    public StorageClient getStorageClient(){
        return StorageClient.getInstance();
    }
}
