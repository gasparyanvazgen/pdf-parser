package io.github.gasparyanvazgen.pdfparser.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseStorageConfig {

    @Value("${firebase.storage.googleApplicationCredentials}")
    private String googleApplicationCredentials;

    @Value("${firebase.storage.storageBucket}")
    private String storageBucket;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(googleApplicationCredentials).getInputStream()))
                .setStorageBucket(storageBucket)
                .build();

        return FirebaseApp.initializeApp(options);
    }

}
