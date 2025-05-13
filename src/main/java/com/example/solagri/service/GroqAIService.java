package com.example.solagri.service;

import com.example.solagri.dto.PredictionExplanationResponse;
import com.example.solagri.dto.PredictionInput;
import com.example.solagri.model.SeedWaterRequirement;
import com.example.solagri.repository.SeedWaterRequirementRepository;
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
import java.util.Optional;

@Service
public class GroqAIService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SeedWaterRequirementRepository seedWaterRequirementRepository;

    public GroqAIService(SeedWaterRequirementRepository seedWaterRequirementRepository) {
        this.seedWaterRequirementRepository = seedWaterRequirementRepository;
    }

    public PredictionExplanationResponse predictSolarPanels(PredictionInput input, String soilType) {
        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        // Log input for debugging
        System.out.println("Input: seed=" + input.getSeed() + ", landSurface=" + input.getLandSurface() +
                ", waterDepth=" + input.getWaterDepth() + ", waterTravelingDistance=" + input.getWaterTravelingDistance() +
                ", soilType=" + (soilType != null ? soilType : "none"));

        // Query water requirement from database
        Optional<SeedWaterRequirement> seedRequirement = seedWaterRequirementRepository.findBySeedType(input.getSeed());
        double waterPerM2;
        if (seedRequirement.isPresent()) {
            waterPerM2 = seedRequirement.get().getWaterPerM2();
        } else {
            waterPerM2 = 500.0; // Default value if seed type not found
            System.err.println("⚠️ Seed type '" + input.getSeed() + "' not found in database. Using default water_per_m2: 500.0");
        }

        // Adjust water requirement based on soil type
        double soilMultiplier = getSoilMultiplier(soilType);
        waterPerM2 *= soilMultiplier;

        String prompt = String.format(
                "Seed type: %s\nLand surface: %.2f m²\nWater depth: %.2f m\nDistance water travels: %.2f m\nWater requirement: %.2f L/m²\nSoil type: %s",
                input.getSeed(), input.getLandSurface(), input.getWaterDepth(), input.getWaterTravelingDistance(), waterPerM2,
                soilType != null ? soilType : "unknown"
        );

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "llama-3.1-8b-instant");
        requestBody.put("temperature", 0.0);
        requestBody.put("top_p", 1.0);

        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", """
                    You are an assistant that calculates solar panel needs for agriculture.
                    Use these exact formulas:
                    1. Water requirement (liters): water_liters = land_surface * water_per_m2.
                    2. Power (kW): power_kW = (water_liters * 1 * 9.81 * (water_depth + water_traveling_distance) / 0.8) / 1000.
                    3. Solar panels: panels = ceil(power_kW / 0.3).
                    4. Surface area (m²): surface_area = panels * 2.
                    5. Cost (MAD): cost = panels * 5000.
                    Input: Seed type: %s, Land surface: %.2f m², Water depth: %.2f m, Distance water travels: %.2f m, Water requirement: %.2f L/m², Soil type: %s.
                    Output ONLY valid JSON:
                    {
                      "kilowatts": double,
                      "panels": int,
                      "surfaceArea": double,
                      "cost": double,
                      "explanation": string
                    }
                    The explanation must be a 500-word string with no unescaped control characters (use \\n for newlines, \\t for tabs).
                    Include the water_liters value and soil type impact in the explanation. Round numbers to 2 decimal places.
                    Ensure JSON is strictly valid and parseable.
                """.formatted(input.getSeed(), input.getLandSurface(), input.getWaterDepth(), input.getWaterTravelingDistance(), waterPerM2, soilType != null ? soilType : "unknown")),
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
                    String content = message.get("content").toString();
                    System.out.println("Raw API response: [" + content + "]");
                    return parsePredictionExplanation(content);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Groq API call failed: " + e.getMessage());
        }

        return new PredictionExplanationResponse("Failed to retrieve prediction due to API error", 0, 0, 0, 0);
    }

    private double getSoilMultiplier(String soilType) {
        if (soilType == null) return 1.0;
        return switch (soilType.toLowerCase()) {
            case "sandy" -> 1.2; // Sandy soil retains less water, needs more
            case "clay" -> 0.8;  // Clay soil retains more water, needs less
            case "loam" -> 1.0;  // Loam is balanced
            default -> 1.0;      // Default for unknown types
        };
    }

    private PredictionExplanationResponse parsePredictionExplanation(String content) throws IOException {
        // Remove surrounding square brackets if present
        String sanitizedContent = content.trim();
        if (sanitizedContent.startsWith("[") && sanitizedContent.endsWith("]")) {
            sanitizedContent = sanitizedContent.substring(1, sanitizedContent.length() - 1).trim();
        }

        // Log sanitized content for debugging
        System.out.println("Sanitized content: [" + sanitizedContent + "]");

        try {
            Map<String, Object> jsonMap = objectMapper.readValue(sanitizedContent, new TypeReference<Map<String, Object>>() {});
            String explanation = jsonMap.get("explanation").toString();
            double kilowatts = Math.round(Double.parseDouble(jsonMap.get("kilowatts").toString()) * 100.0) / 100.0;
            int panels = Integer.parseInt(jsonMap.get("panels").toString());
            double surfaceArea = Math.round(Double.parseDouble(jsonMap.get("surfaceArea").toString()) * 100.0) / 100.0;
            double cost = Math.round(Double.parseDouble(jsonMap.get("cost").toString()) * 100.0) / 100.0;

            return new PredictionExplanationResponse(explanation, kilowatts, panels, surfaceArea, cost);
        } catch (IOException e) {
            System.err.println("❌ JSON parsing failed for sanitized content: [" + sanitizedContent + "]");
            throw new IOException("Failed to parse API response: " + e.getMessage(), e);
        }
    }
}