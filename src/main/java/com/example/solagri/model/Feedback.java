package com.example.solagri.model;

import jakarta.persistence.*;

@Entity
@Table(name = "feedbacks")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String feedbackMessage;

    @Column(nullable = false)
    private double rating; // Rating scale (e.g., 1 to 5)

    @ManyToOne
    @JoinColumn(name = "prediction_id", nullable = false)
    private Prediction prediction;

    public Feedback() {}

    public Feedback(String feedbackMessage, double rating, Prediction prediction) {
        this.feedbackMessage = feedbackMessage;
        this.rating = rating;
        this.prediction = prediction;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFeedbackMessage() {
        return feedbackMessage;
    }
    public void setFeedbackMessage(String feedbackMessage) {
        this.feedbackMessage = feedbackMessage;
    }

    public double getRating() {
        return rating;
    }
    public void setRating(double rating) {
        this.rating = rating;
    }

    public Prediction getPrediction() {
        return prediction;
    }
    public void setPrediction(Prediction prediction) {
        this.prediction = prediction;
    }
}