package com.example.solagri.dto;

public class PredictionExplanationResponse {
    private String explanation;
    private double kilowatts;
    private int panels;
    private double surfaceArea;
    private double cost;

    // Constructor
    public PredictionExplanationResponse(String explanation, double kilowatts, int panels, double surfaceArea, double cost) {
        this.explanation = explanation;
        this.kilowatts = kilowatts;
        this.panels = panels;
        this.surfaceArea = surfaceArea;
        this.cost = cost;
    }

    // Getters and Setters
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public double getKilowatts() { return kilowatts; }
    public void setKilowatts(double kilowatts) { this.kilowatts = kilowatts; }

    public int getPanels() { return panels; }
    public void setPanels(int panels) { this.panels = panels; }

    public double getSurfaceArea() { return surfaceArea; }
    public void setSurfaceArea(double surfaceArea) { this.surfaceArea = surfaceArea; }

    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}
