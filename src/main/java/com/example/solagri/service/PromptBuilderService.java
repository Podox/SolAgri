package com.example.solagri.service;

import com.example.solagri.dto.PredictionRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class PromptBuilderService {

    public String buildPrompt(PredictionRequest request) {
        return String.format(
                "Calculate the solar panel requirements for the following:\n" +
                        "Seed Type: %s\n" +
                        "Well Depth: %.2f meters\n" +
                        "Water Distance: %.2f meters\n" +
                        "Land Surface: %.2f square meters\n" +
                        "Use fixed formulas and deterministic logic to return only the JSON with: " +
                        "{ \"kilowatts\": ..., \"panels\": ..., \"surfaceArea\": ..., \"cost\": ... }",
                request.getSeedType(),
                request.getWellDepth(),
                request.getWaterDistance(),
                request.getLandSurface()
        );
    }
}
