package com.example.ragdemo.service;

import com.example.ragdemo.entity.Document;
import com.example.ragdemo.entity.DocumentChunk;
import com.example.ragdemo.repository.DocumentRepository;
import com.example.ragdemo.repository.DocumentChunkRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final PdfService pdfService;
    private final GeminiService geminiService;

    @Value("${rag.chunk-size}")
    private Integer chunkSize;

    @Value("${rag.chunk-overlap}")
    private Integer chunkOverlap;

    public DocumentService(DocumentRepository documentRepository,
            DocumentChunkRepository documentChunkRepository,
            PdfService pdfService,
            GeminiService geminiService) {
        this.documentRepository = documentRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.pdfService = pdfService;
        this.geminiService = geminiService;
    }

    public Document uploadDocument(MultipartFile file, String source) throws IOException {
        String fileName = file.getOriginalFilename();
        String fileType = getFileExtension(fileName);
        String content = extractContent(file, fileType);
        Long fileSize = file.getSize();

        Document document = new Document(fileName, fileType, content, fileSize, source);
        document = documentRepository.save(document);

        // Process document chunks and embeddings
        processDocumentChunks(document, content);

        return document;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "unknown";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private String extractContent(MultipartFile file, String fileType) throws IOException {
        switch (fileType.toLowerCase()) {
            case "pdf":
                return pdfService.extractTextFromPdf(file);
            case "txt":
                return new String(file.getBytes());
            default:
                throw new UnsupportedOperationException("Unsupported file type: " + fileType);
        }
    }

    private void processDocumentChunks(Document document, String content) {
        // Simple text chunking
        List<String> chunks = splitTextIntoChunks(content, chunkSize, chunkOverlap);

        try {
            // Generate embeddings for all chunks using Gemini batch API
            List<List<Float>> embeddings = geminiService.generateEmbeddings(chunks);

            // Verify embeddings match chunks
            if (embeddings.size() != chunks.size()) {
                throw new RuntimeException(String.format(
                        "Embeddings count (%d) doesn't match chunks count (%d)",
                        embeddings.size(), chunks.size()));
            }

            // Process each chunk with its embedding
            for (int i = 0; i < chunks.size(); i++) {
                String chunkContent = chunks.get(i);
                List<Float> embedding = embeddings.get(i);

                // Create and save document chunk
                DocumentChunk chunk = new DocumentChunk(document, chunkContent, i);
                chunk.setEmbedding(embedding);
                documentChunkRepository.save(chunk);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate embeddings for document chunks", e);
        }
    }

    private List<String> splitTextIntoChunks(String text, int chunkSize, int overlap) {
        logger.info("Splitting text: length={}, chunkSize={}, overlap={}", text.length(), chunkSize, overlap);
        List<String> chunks = new java.util.ArrayList<>();
        int start = 0;
        // Parameter validation
        if (chunkSize <= 0)
            chunkSize = 1000;
        if (overlap < 0)
            overlap = 0;
        if (overlap >= chunkSize)
            overlap = 0;

        while (start < text.length()) {
            int end = Math.min(start + chunkSize, text.length());
            String chunk = text.substring(start, end);
            chunks.add(chunk);

            if (end == text.length())
                break;
            int nextStart = end - overlap;
            // Ensure start always moves forward
            if (nextStart <= start) {
                nextStart = end;
            }
            start = nextStart;
        }
        logger.info("Chunking complete: totalChunks={}", chunks.size());
        return chunks;
    }

    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Transactional
    public void deleteDocument(Long id) {
        // First delete all chunks associated with this document
        documentChunkRepository.deleteByDocumentId(id);
        // Then delete the document
        documentRepository.deleteById(id);
    }
}