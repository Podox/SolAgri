package com.example.solagri.dto;

public class AdvancedPredictionInput {
    private Long predictionId;
    private String soilType;

    // Getters and Setters
    public Long getPredictionId() { return predictionId; }
    public void setPredictionId(Long predictionId) { this.predictionId = predictionId; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }
}
