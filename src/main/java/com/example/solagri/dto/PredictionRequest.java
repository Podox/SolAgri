package com.example.solagri.dto;

public class PredictionRequest {
    private String seedType;
    private double wellDepth;
    private double waterDistance;
    private double landSurface;

    public PredictionRequest() {}

    public PredictionRequest(String seedType, double wellDepth, double waterDistance, double landSurface) {
        this.seedType = seedType;
        this.wellDepth = wellDepth;
        this.waterDistance = waterDistance;
        this.landSurface = landSurface;
    }

    public String getSeedType() { return seedType; }
    public void setSeedType(String seedType) { this.seedType = seedType; }

    public double getWellDepth() { return wellDepth; }
    public void setWellDepth(double wellDepth) { this.wellDepth = wellDepth; }

    public double getWaterDistance() { return waterDistance; }
    public void setWaterDistance(double waterDistance) { this.waterDistance = waterDistance; }

    public double getLandSurface() { return landSurface; }
    public void setLandSurface(double landSurface) { this.landSurface = landSurface; }
}
