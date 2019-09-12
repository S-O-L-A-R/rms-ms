package com.solar.ms.rms.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id : vaulted-channel-252309}")
    private String projectId;

    @Bean
    @ConditionalOnMissingBean
    public Firestore getFirestore() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

//        InputStream serviceAccount = new FileInputStream(ResourceUtils.getFile("classpath:firebase-sa.json"));
//        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
//                .setDatabaseUrl("https://vaulted-channel-252309.firebaseio.com")
                .build();
        FirebaseApp.initializeApp(options);

        return FirestoreClient.getFirestore();
    }
}
