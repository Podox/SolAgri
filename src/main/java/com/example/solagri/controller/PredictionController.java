package com.example.solagri.controller;

import com.example.solagri.dto.PredictionResponse;
import com.example.solagri.service.GroqAIService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/solar")
public class PredictionController {

    private final GroqAIService groqAIService;

    public PredictionController(GroqAIService groqAIService) {
        this.groqAIService = groqAIService;
    }

    @PostMapping("/predict")
    public PredictionResponse predict(@RequestBody PromptRequest request) {
        return groqAIService.predictSolarPanels(request.getPrompt());
    }

    static class PromptRequest {
        private String prompt;
        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
    }
}
