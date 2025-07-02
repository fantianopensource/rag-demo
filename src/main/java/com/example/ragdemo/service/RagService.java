package com.example.ragdemo.service;

import com.example.ragdemo.dto.RagResponse;
import com.example.ragdemo.dto.Source;
import com.example.ragdemo.repository.DocumentChunkRepository;
import com.example.ragdemo.repository.DocumentChunkRepository.DocumentChunkWithSimilarity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final DocumentChunkRepository documentChunkRepository;
    private final GeminiService geminiService;

    @Value("${rag.similarity-threshold}")
    private Double similarityThreshold;

    @Value("${rag.max-results}")
    private Integer maxResults;

    public RagService(DocumentChunkRepository documentChunkRepository,
            GeminiService geminiService) {
        this.documentChunkRepository = documentChunkRepository;
        this.geminiService = geminiService;
    }

    public RagResponse answerQuestionWithSources(String question) {
        try {
            // Generate embedding for the question using Gemini
            // Use RETRIEVAL_QUERY taskType for better query-document matching
            List<Float> questionEmbedding = geminiService.generateQueryEmbedding(question);
            String embeddingStr = "["
                    + questionEmbedding.stream().map(Object::toString).collect(Collectors.joining(",")) + "]";

            // Retrieve similar chunks
            List<DocumentChunkWithSimilarity> similarChunks = documentChunkRepository.findSimilarChunks(
                    embeddingStr, similarityThreshold, maxResults);

            if (similarChunks.isEmpty()) {
                return new RagResponse(
                        "Sorry, I couldn't find relevant information in the knowledge base to answer your question.",
                        List.of());
            }

            // Build context from retrieved chunks
            String context = similarChunks.stream()
                    .map(chunk -> String.format("Content: %s\n", chunk.getContent()))
                    .collect(java.util.stream.Collectors.joining("\n"));

            // Create prompt with context
            String prompt = String.format(
                    """
                            Answer the question based on the following knowledge base content. If there is no relevant information in the knowledge base, please clearly state that.

                            Knowledge Base Content:
                            %s

                            Question: %s

                            Please answer in English:
                            """,
                    context, question);

            // Generate answer
            String answer = geminiService.callGemini(prompt);

            // Prepare sources
            List<Source> sources = similarChunks.stream()
                    .map(chunk -> new Source(
                            chunk.getFileName(),
                            chunk.getContent(),
                            chunk.getSimilarity()))
                    .collect(java.util.stream.Collectors.toList());

            return new RagResponse(answer, sources);
        } catch (Exception e) {
            return new RagResponse(
                    "Sorry, there was an error generating the answer: " + e.getMessage(),
                    List.of());
        }
    }

}