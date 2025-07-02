package com.example.ragdemo.dto;

public class QuestionRequest {
    private String question;

    // Default constructor for JSON deserialization
    public QuestionRequest() {
    }

    public QuestionRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}