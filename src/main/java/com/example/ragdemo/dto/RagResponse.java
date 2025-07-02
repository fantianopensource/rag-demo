package com.example.ragdemo.dto;

import java.util.List;

public class RagResponse {
    private String answer;
    private List<Source> sources;

    public RagResponse(String answer, List<Source> sources) {
        this.answer = answer;
        this.sources = sources;
    }

    // Default constructor for JSON deserialization
    public RagResponse() {
    }

    // Getters and setters
    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }
}