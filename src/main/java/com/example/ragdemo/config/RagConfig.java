package com.example.ragdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.http.HttpClient;

@Configuration
public class RagConfig {
    @Value("${google.api-key}")
    private String googleApiKey;

    @Value("${google.model}")
    private String googleModel;

    @Value("${google.embedding-model}")
    private String googleEmbeddingModel;

    @Bean
    public HttpClient httpClient() {
        return HttpClient.newHttpClient();
    }

    public String getGeminiApiEndpoint() {
        return "https://generativelanguage.googleapis.com/v1beta/models/" + googleModel + ":generateContent?key="
                + googleApiKey;
    }

    public String getBatchEmbeddingEndpoint() {
        return "https://generativelanguage.googleapis.com/v1beta/models/" + googleEmbeddingModel
                + ":batchEmbedContents?key="
                + googleApiKey;
    }

    public String getSingleEmbeddingEndpoint() {
        return "https://generativelanguage.googleapis.com/v1beta/models/" + googleEmbeddingModel + ":embedContent?key="
                + googleApiKey;
    }

    public String getGoogleApiKey() {
        return googleApiKey;
    }

    public String getGoogleModel() {
        return googleModel;
    }

    public String getGoogleEmbeddingModel() {
        return googleEmbeddingModel;
    }
}