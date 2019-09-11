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

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("${firebase.project-id : vaulted-channel-252309}")
    private String projectId;

    @Bean
    @ConditionalOnMissingBean
    public Firestore getFirestore() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();

//        InputStream serviceAccount = new FileInputStream("C:\\Users\\umami\\Documents\\workspace\\solar\\firebase-sa.json");
//        GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build();
        FirebaseApp.initializeApp(options);

        return FirestoreClient.getFirestore();
    }
}
