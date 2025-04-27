package com.example.solagri.dto;

public class PredictionInput {
    private String seed;
    private double landSurface;
    private double waterDepth;
    private double waterTravelingDistance;

    // Getters and Setters
    public String getSeed() { return seed; }
    public void setSeed(String seed) { this.seed = seed; }

    public double getLandSurface() { return landSurface; }
    public void setLandSurface(double landSurface) { this.landSurface = landSurface; }

    public double getWaterDepth() { return waterDepth; }
    public void setWaterDepth(double waterDepth) { this.waterDepth = waterDepth; }

    public double getWaterTravelingDistance() { return waterTravelingDistance; }
    public void setWaterTravelingDistance(double waterTravelingDistance) { this.waterTravelingDistance = waterTravelingDistance; }
}
