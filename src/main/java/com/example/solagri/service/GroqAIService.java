package com.example.solagri.service;

import com.example.solagri.dto.PredictionExplanationResponse;
import com.example.solagri.dto.PredictionInput;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GroqAIService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    public PredictionExplanationResponse predictSolarPanels(PredictionInput input) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        // Build prompt from structured input
        String prompt = String.format(
                "Seed type: %s\nLand surface: %.2f m²\nWater depth: %.2f m\nDistance water travels: %.2f m",
                input.getSeed(),
                input.getLandSurface(),
                input.getWaterDepth(),
                input.getWaterTravelingDistance()
        );

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
              "cost": double,
              "explanation": string
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
                    return parsePredictionExplanation(content);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Groq API call failed: " + e.getMessage());
        }

        return new PredictionExplanationResponse("Failed to retrieve prediction", 0, 0, 0, 0);
    }

    private PredictionExplanationResponse parsePredictionExplanation(String content) throws IOException {
        // Use ObjectMapper to parse JSON into PredictionExplanationResponse
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(content, new TypeReference<Map<String, Object>>() {});
        String explanation = jsonMap.get("explanation").toString();
        double kilowatts = Double.parseDouble(jsonMap.get("kilowatts").toString());
        int panels = Integer.parseInt(jsonMap.get("panels").toString());
        double surfaceArea = Double.parseDouble(jsonMap.get("surfaceArea").toString());
        double cost = Double.parseDouble(jsonMap.get("cost").toString());

        return new PredictionExplanationResponse(explanation, kilowatts, panels, surfaceArea, cost);
    }
}
