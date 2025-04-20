package com.example.solagri.service;

import com.example.solagri.dto.PredictionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqAIService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PredictionResponse predictSolarPanels(String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.1-8b-instant");

        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", """
                    You are an assistant that helps calculate solar panel needs for agriculture.
                    Given the seed type, land surface, water depth, and distance the water needs to travel,
                    calculate the following using fixed formulas:
                    - Total water requirement in liters
                    - Required power in kilowatts (kW)
                    - Number of solar panels needed
                    - Total solar surface area (in square meters)
                    - Estimated cost (in MAD)

                    Output ONLY valid JSON in the format:
                    {
                      "kilowatts": double,
                      "panels": int,
                      "surfaceArea": double,
                      "cost": double
                    }
                    Do not include any extra text, only valid JSON.
                """),
                Map.of("role", "user", "content", prompt)
        ));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            Map<?, ?> responseBody = response.getBody();

            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = message.get("content").toString().trim();

                    // Parse the AI's JSON string into our DTO
                    return objectMapper.readValue(content, PredictionResponse.class);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Groq API call failed: " + e.getMessage());
        }

        // Optional fallback object (you can customize this)
        return new PredictionResponse(0, 0, 0, 0);
    }
}
