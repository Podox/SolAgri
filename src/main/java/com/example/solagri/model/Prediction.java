package com.example.solagri.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "predictions")
public class Prediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String seed;
    private double landSurface;
    private double waterDepth;
    private double waterTravelingDistance;

    private String explanation;
    private double kilowatts;
    private int panels;
    private double surfaceArea;
    private double cost;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "prediction", cascade = CascadeType.ALL, orphanRemoval = true)
    private AdvancedPrediction advancedPrediction;
    @OneToMany(mappedBy = "prediction", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Feedback> feedbacks = new ArrayList<>();

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public void addFeedback(Feedback feedback) {
        feedbacks.add(feedback);
        feedback.setPrediction(this);
    }

    public void removeFeedback(Feedback feedback) {
        feedbacks.remove(feedback);
        feedback.setPrediction(null);
    }
    // Constructors
    public Prediction() {}

    public Prediction(String seed, double landSurface, double waterDepth, double waterTravelingDistance,
                      String explanation, double kilowatts, int panels, double surfaceArea, double cost, User user) {
        this.seed = seed;
        this.landSurface = landSurface;
        this.waterDepth = waterDepth;
        this.waterTravelingDistance = waterTravelingDistance;
        this.explanation = explanation;
        this.kilowatts = kilowatts;
        this.panels = panels;
        this.surfaceArea = surfaceArea;
        this.cost = cost;
        this.user = user;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSeed() { return seed; }
    public void setSeed(String seed) { this.seed = seed; }

    public double getLandSurface() { return landSurface; }
    public void setLandSurface(double landSurface) { this.landSurface = landSurface; }

    public double getWaterDepth() { return waterDepth; }
    public void setWaterDepth(double waterDepth) { this.waterDepth = waterDepth; }

    public double getWaterTravelingDistance() { return waterTravelingDistance; }
    public void setWaterTravelingDistance(double waterTravelingDistance) { this.waterTravelingDistance = waterTravelingDistance; }

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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public AdvancedPrediction getAdvancedPrediction() { return advancedPrediction; }
    public void setAdvancedPrediction(AdvancedPrediction advancedPrediction) { this.advancedPrediction = advancedPrediction; }
}