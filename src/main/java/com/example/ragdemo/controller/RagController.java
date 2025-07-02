package com.example.ragdemo.controller;

import com.example.ragdemo.dto.QuestionRequest;
import com.example.ragdemo.dto.RagResponse;
import com.example.ragdemo.service.RagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rag")
@CrossOrigin(origins = "*")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/ask-with-sources")
    public ResponseEntity<RagResponse> askQuestionWithSources(@RequestBody QuestionRequest request) {
        RagResponse response = ragService.answerQuestionWithSources(request.getQuestion());
        return ResponseEntity.ok(response);
    }
}