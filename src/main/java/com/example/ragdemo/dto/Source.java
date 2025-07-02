package com.example.ragdemo.dto;

public class Source {
    private String fileName;
    private String content;
    private Double similarity;

    public Source(String fileName, String content, Double similarity) {
        this.fileName = fileName;
        this.content = content;
        this.similarity = similarity;
    }

    // Default constructor for JSON deserialization
    public Source() {
    }

    // Getters and setters
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }
}