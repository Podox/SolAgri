package com.example.solagri.repository;

import com.example.solagri.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    // Fetch all feedback for a specific prediction
    List<Feedback> findByPredictionId(Long predictionId);
}