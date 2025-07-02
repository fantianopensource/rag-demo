package com.example.ragdemo.service;

import com.example.ragdemo.config.RagConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.ArrayList;

@Service
public class GeminiService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final RagConfig ragConfig;

    public GeminiService(HttpClient httpClient, ObjectMapper objectMapper, RagConfig ragConfig) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.ragConfig = ragConfig;
    }

    public String callGemini(String userPrompt) throws Exception {
        // Build request body
        ObjectNode part = objectMapper.createObjectNode();
        part.put("text", userPrompt);

        ObjectNode content = objectMapper.createObjectNode();
        content.putArray("parts").add(part);

        ObjectNode root = objectMapper.createObjectNode();
        root.putArray("contents").add(content);

        String requestBody = objectMapper.writeValueAsString(root);

        // Send HTTP request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ragConfig.getGeminiApiEndpoint()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse Gemini API response and extract text content
        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode candidates = rootNode.path("candidates");
        if (candidates.isArray() && candidates.size() > 0) {
            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (parts.isArray() && parts.size() > 0) {
                return parts.get(0).path("text").asText();
            }
        }
        return "[No valid response from Gemini API]";
    }

    /**
     * Generate embedding for a query using Gemini embedding API
     * This method uses RETRIEVAL_QUERY taskType which is optimized for search
     * queries
     */
    public List<Float> generateQueryEmbedding(String query) throws Exception {
        // Build embedding request body
        ObjectNode part = objectMapper.createObjectNode();
        part.put("text", query);

        ObjectNode content = objectMapper.createObjectNode();
        content.putArray("parts").add(part);

        ObjectNode root = objectMapper.createObjectNode();
        root.put("model", "models/" + ragConfig.getGoogleEmbeddingModel());
        root.set("content", content);
        root.put("taskType", "RETRIEVAL_QUERY");

        String requestBody = objectMapper.writeValueAsString(root);

        // Send HTTP request to embedding endpoint
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ragConfig.getSingleEmbeddingEndpoint()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Parse embedding response
        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode embedding = rootNode.path("embedding");
        JsonNode values = embedding.path("values");

        List<Float> embeddingVector = new ArrayList<>();
        if (values.isArray()) {
            for (JsonNode value : values) {
                embeddingVector.add((float) value.asDouble());
            }
        }

        return embeddingVector;
    }

    /**
     * Generate embeddings for multiple texts using Gemini batch embedding API
     */
    public List<List<Float>> generateEmbeddings(List<String> texts) throws Exception {
        List<List<Float>> embeddings = new ArrayList<>();

        // Process texts in batches of 250 (API limit)
        int batchSize = 250;
        for (int i = 0; i < texts.size(); i += batchSize) {
            int end = Math.min(i + batchSize, texts.size());
            List<String> batch = texts.subList(i, end);

            // Build batch request
            ObjectNode root = objectMapper.createObjectNode();
            ArrayNode requestsArray = root.putArray("requests");

            for (String text : batch) {
                ObjectNode request = objectMapper.createObjectNode();
                request.put("model", "models/" + ragConfig.getGoogleEmbeddingModel());

                ObjectNode part = objectMapper.createObjectNode();
                part.put("text", text);

                ObjectNode content = objectMapper.createObjectNode();
                content.putArray("parts").add(part);

                request.set("content", content);
                request.put("taskType", "RETRIEVAL_DOCUMENT");

                requestsArray.add(request);
            }

            String requestBody = objectMapper.writeValueAsString(root);

            // Send batch request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ragConfig.getBatchEmbeddingEndpoint()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Log response for debugging
            System.out.println("API Response: " + response.body());

            // Parse batch response
            JsonNode rootNode = objectMapper.readTree(response.body());
            JsonNode embeddingsArray = rootNode.path("embeddings");

            if (embeddingsArray.isArray()) {
                for (JsonNode embeddingNode : embeddingsArray) {
                    JsonNode values = embeddingNode.path("values");
                    List<Float> embeddingVector = new ArrayList<>();
                    if (values.isArray()) {
                        for (JsonNode value : values) {
                            embeddingVector.add((float) value.asDouble());
                        }
                    }
                    embeddings.add(embeddingVector);
                }
            } else {
                // If no embeddings returned, throw exception with response details
                throw new RuntimeException("No embeddings returned from API. Response: " + response.body());
            }
        }

        // Verify that we got the expected number of embeddings
        if (embeddings.size() != texts.size()) {
            throw new RuntimeException(String.format(
                    "Expected %d embeddings but got %d. This indicates an API issue.",
                    texts.size(), embeddings.size()));
        }

        return embeddings;
    }
}