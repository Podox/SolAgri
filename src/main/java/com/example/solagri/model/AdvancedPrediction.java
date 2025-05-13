package com.example.solagri.model;

import jakarta.persistence.*;

@Entity
@Table(name = "advanced_predictions")
public class AdvancedPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "soil_type", nullable = false)
    private String soilType;

    @OneToOne
    @JoinColumn(name = "prediction_id", nullable = false)
    private Prediction prediction;

    // Constructors
    public AdvancedPrediction() {}

    public AdvancedPrediction(String soilType, Prediction prediction) {
        this.soilType = soilType;
        this.prediction = prediction;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSoilType() { return soilType; }
    public void setSoilType(String soilType) { this.soilType = soilType; }

    public Prediction getPrediction() { return prediction; }
    public void setPrediction(Prediction prediction) { this.prediction = prediction; }
}